package org.stockify.model.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.dto.response.NotificationResponse;
import org.stockify.model.entity.NotificationEntity;
import org.stockify.model.entity.NotificationHidden;
import org.stockify.model.entity.NotificationRead;
import org.stockify.model.enums.NotificationStatus;
import org.stockify.model.enums.NotificationType;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.NotificationMapper;
import org.stockify.model.repository.NotificationHiddenRepository;
import org.stockify.model.repository.NotificationReadRepository;
import org.stockify.model.repository.NotificationRepository;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final NotificationHiddenRepository notificationHiddenRepository;
    private final Map<Long, SseEmitter> activeEmitters = new ConcurrentHashMap<>();

    // SSE
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(3600000L);
        activeEmitters.put(userId, emitter);

        emitter.onCompletion(() -> activeEmitters.remove(userId));
        emitter.onTimeout(() -> activeEmitters.remove(userId));
        emitter.onError(e -> activeEmitters.remove(userId));

        return emitter;
    }

    // CRUD
    public Page<NotificationResponse> findByUserId(long userId, Pageable pageable) {
        return notificationRepository.findAllProjectedByUserId(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    public NotificationResponse findById(Long id) {
        return notificationMapper.toResponse(resolveNotification(id));
    }

    public NotificationResponse createNotification(NotificationRequest request) {
        checkRequestIntegrity(request);
        NotificationEntity entity = notificationMapper.toEntity(request);
        return getNotificationResponse(entity);
    }

    public NotificationResponse patchNotification(Long id, NotificationRequest request) {
        NotificationEntity entity = resolveNotification(id);
        checkRequestIntegrity(request);
        notificationMapper.patchEntityFromRequest(request, entity);
        return saveAndMaybeDispatch(entity);
    }

    public NotificationResponse updateNotification(Long id, NotificationRequest request) {
        NotificationEntity entity = resolveNotification(id);
        checkRequestIntegrity(request);
        notificationMapper.updateEntityFromRequest(request, entity);
        return saveAndMaybeDispatch(entity);
    }

    public void deleteNotification(Long id) {
        notificationRepository.delete(resolveNotification(id));
    }

    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        NotificationEntity entity = resolveNotification(notificationId);
        if (!notificationReadRepository.existsByUserIdAndNotificationId(userId, notificationId)) {
            NotificationRead readEntry = NotificationRead.builder()
                    .userId(userId)
                    .notificationId(notificationId)
                    .build();
            notificationReadRepository.save(readEntry);
        }
        NotificationResponse response = notificationMapper.toResponse(entity);
        return markResponseAsRead(response);
    }

    public NotificationResponse hide(Long userId, Long notificationId) {
        NotificationEntity entity = resolveNotification(notificationId);
        if (!notificationHiddenRepository.existsByUserIdAndNotificationId(userId, notificationId)) {
            NotificationHidden hidden = new NotificationHidden();
            hidden.setUserId(userId);
            hidden.setNotificationId(notificationId);
            notificationHiddenRepository.save(hidden);
        }
        NotificationResponse response = notificationMapper.toResponse(entity);
        return markResponseAsHidden(response);
    }

    // Helpers

    @Scheduled(fixedRate = 60000)
    public void publishScheduledNotifications() {
        Instant now = Instant.now();
        List<NotificationEntity> pending = notificationRepository.findAllByStatusAndPublishAtBefore(NotificationStatus.PENDING, now);
        pending.forEach(entity -> {
            if (entity.getExpiresAt() != null && !entity.getExpiresAt().isAfter(now)) {
                return;
            }
            entity.setStatus(NotificationStatus.PUBLISHED);
            notificationRepository.save(entity);
            NotificationResponse response = notificationMapper.toResponse(entity);
            dispatchToTargets(response, entity.getTargetUserId(), entity.getType());
        });
    }

    private boolean isReadyToPublish(Instant publishAt) {
        return publishAt == null || !publishAt.isAfter(Instant.now());
    }

    private NotificationResponse saveAndMaybeDispatch(NotificationEntity entity) {
        return getNotificationResponse(entity);
    }

    private NotificationResponse getNotificationResponse(NotificationEntity entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(NotificationStatus.PENDING);
        }
        if (entity.getStatus() == NotificationStatus.PENDING && isReadyToPublish(entity.getPublishAt())) {
            entity.setStatus(NotificationStatus.PUBLISHED);
        }
        NotificationEntity saved = notificationRepository.save(entity);
        NotificationResponse response = notificationMapper.toResponse(saved);
        if (saved.getStatus() == NotificationStatus.PUBLISHED) {
            dispatchToTargets(response, saved.getTargetUserId(), saved.getType());
        }
        return response;
    }

    private void dispatchToTargets(NotificationResponse notification, Long targetUserId, NotificationType type) {
        if (targetUserId != null) {
            sendToUser(targetUserId, notification);
            return;
        }

        activeEmitters.keySet().forEach(connectedUserId -> {
            boolean shouldSend = type == NotificationType.GLOBAL || type == NotificationType.PRODUCT_ALERT;
            if (shouldSend) {
                sendToUser(connectedUserId, notification);
            }
        });
    }

    private void sendToUser(Long userId, NotificationResponse notification) {
        SseEmitter emitter = activeEmitters.get(userId);
        if (emitter == null) return;

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
        } catch (IOException e) {
            activeEmitters.remove(userId);
        }
    }

    private void checkRequestIntegrity(NotificationRequest request) {
        if (request.relatedProductId() != null) {
            resolveProduct(request.relatedProductId());
        }
        if (request.targetUserId() != null) {
            resolveUser(request.targetUserId());
        }
    }

    private void resolveProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product with id " + productId + " not found");
        }
    }

    private void resolveUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    private NotificationEntity resolveNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification with ID " + id + " not found"));
    }

    private NotificationResponse markResponseAsRead(NotificationResponse response) {
        return new NotificationResponse(
                response.id(),
                response.title(),
                response.message(),
                response.type(),
                response.icon(),
                response.relatedProductId(),
                response.publishAt(),
                response.createdAt(),
                response.hidden(),
                true
        );
    }

    private NotificationResponse markResponseAsHidden(NotificationResponse response) {
        return new NotificationResponse(
                response.id(),
                response.title(),
                response.message(),
                response.type(),
                response.icon(),
                response.relatedProductId(),
                response.publishAt(),
                response.createdAt(),
                true,
                response.read()
        );
    }
}
