package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.stockify.dto.request.notifications.NotificationRequest;
import org.stockify.dto.response.NotificationResponse;
import org.stockify.model.service.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Notifications", description = "Endpoints for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getAll(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(notificationService.findAll(pageable));
    }


    @Operation(summary = "Get notifications visible for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paged notifications returned successfully"),
            @ApiResponse(responseCode = "404", description = "No notifications found for the user", content = @Content)
    })

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getByUser(
            @Parameter(description = "User identifier", example = "12")
            @PathVariable Long userId,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(notificationService.findByUserId(userId, pageable));
    }

    @Operation(summary = "Create a notification")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @Valid @RequestBody NotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a notification (full update)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification updated successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> update(
            @Parameter(description = "Notification identifier", example = "100")
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.updateNotification(id, request));
    }

    @Operation(summary = "Patch a notification (partial update)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification patched successfully"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<NotificationResponse> patch(
            @Parameter(description = "Notification identifier", example = "100")
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(notificationService.patchNotification(id, request));
    }

    @Operation(summary = "Delete a notification")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Notification deleted"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Notification identifier", example = "100")
            @PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Mark a notification as read for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    @PatchMapping("/user/{userId}/read/{notificationId}")
    public ResponseEntity<NotificationResponse> markAsRead(
            @Parameter(description = "User identifier", example = "12")
            @PathVariable Long userId,
            @Parameter(description = "Notification identifier", example = "100")
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.markAsRead(userId, notificationId));
    }

    @Operation(summary = "Hide a notification for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notification hidden for the user"),
            @ApiResponse(responseCode = "404", description = "Notification not found", content = @Content)
    })
    @PatchMapping("/user/{userId}/hide/{notificationId}")
    public ResponseEntity<NotificationResponse> hide(
            @Parameter(description = "User identifier", example = "12")
            @PathVariable Long userId,
            @Parameter(description = "Notification identifier", example = "100")
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.hide(userId, notificationId));
    }

    @Operation(summary = "Open an SSE stream for real-time notifications")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSE stream opened"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping(value = "/user/{userId}/stream", produces = "text/event-stream")
    public SseEmitter stream(
            @Parameter(description = "User identifier", example = "12")
            @PathVariable Long userId) {
        return notificationService.subscribe(userId);
    }
}
