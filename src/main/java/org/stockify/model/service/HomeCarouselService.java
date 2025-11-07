package org.stockify.model.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stockify.dto.response.StoreResponse;
import org.stockify.dto.shared.HomeCarouselItem;
import org.stockify.model.entity.CarouselItem;
import org.stockify.model.entity.StoreEntity;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.StoreMapper;
import org.stockify.model.repository.CarouselItemRepository;
import org.stockify.model.repository.StoreRepository;

/**
 * Handles CRUD operations for the singleton store home carousel.
 */
@Service
@RequiredArgsConstructor
public class HomeCarouselService {

    private final StoreRepository storeRepository;
    private final CarouselItemRepository carouselItemRepository;
    private final StoreMapper storeMapper;

    /**
     * Replaces the whole carousel collection with the provided items.
     *
     * @return store response reflecting the new carousel state
     */
    public StoreResponse replaceCarousel(Long storeId, List<HomeCarouselItem> items) {
        StoreEntity store = getStoreById(storeId);
        List<CarouselItem> target = store.getHomeCarousel();
        if (target == null) {
            target = new ArrayList<>();
            store.setHomeCarousel(target);
        }
        target.clear();
        if (items != null) {
            for (HomeCarouselItem dto : items) {
                CarouselItem entity = storeMapper.toEmbeddable(dto);
                entity.setStore(store);
                target.add(entity);
            }
        }
        StoreEntity persisted = storeRepository.save(store);
        return storeMapper.toResponse(persisted);
    }

    /**
     * Returns all carousel entries for the store.
     */
    public List<HomeCarouselItem> listCarousel(Long storeId) {
        StoreEntity store = getStoreById(storeId);
        return store.getHomeCarousel() == null ? List.of()
                : store.getHomeCarousel().stream()
                        .map(storeMapper::toDto)
                        .toList();
    }

    /**
     * Retrieves a single carousel item.
     */
    public HomeCarouselItem findCarouselItem(Long storeId, Long itemId) {
        return storeMapper.toDto(getCarouselItem(storeId, itemId));
    }

    /**
     * Creates a new carousel item and assigns it to the singleton store.
     */
    public HomeCarouselItem createCarouselItem(Long storeId, HomeCarouselItem dto) {
        StoreEntity store = getStoreById(storeId);
        CarouselItem entity = storeMapper.toEmbeddable(dto);
        entity.setStore(store);
        CarouselItem saved = carouselItemRepository.save(entity);
        store.getHomeCarousel().add(saved);
        return storeMapper.toDto(saved);
    }

    /**
     * Updates carousel item fields. When DTO omits a field, it will be set to null.
     */
    public HomeCarouselItem updateCarouselItem(Long storeId, Long itemId, HomeCarouselItem dto) {
        CarouselItem entity = getCarouselItem(storeId, itemId);
        entity.setUrl(dto.url());
        entity.setTitle(dto.title());
        entity.setHref(dto.href());
        CarouselItem saved = carouselItemRepository.save(entity);
        return storeMapper.toDto(saved);
    }

    /**
     * Deletes the carousel item from the store.
     */
    public void deleteCarouselItem(Long storeId, Long itemId) {
        CarouselItem entity = getCarouselItem(storeId, itemId);
        carouselItemRepository.delete(entity);
    }

    private CarouselItem getCarouselItem(Long storeId, Long itemId) {
        return carouselItemRepository.findByIdAndStoreId(itemId, storeId)
                .orElseThrow(() -> new NotFoundException(
                        "Carousel item %d not found for store %d".formatted(itemId, storeId)));
    }

    private StoreEntity getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("Store not found with id: " + storeId));
    }
}
