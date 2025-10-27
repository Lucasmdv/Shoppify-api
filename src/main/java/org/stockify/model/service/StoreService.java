package org.stockify.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.store.StoreRequest;
import org.stockify.dto.response.StoreResponse;
import org.stockify.dto.shared.HomeCarouselItem;
import org.stockify.model.entity.StoreEntity;
import org.stockify.model.exception.DuplicatedUniqueConstraintException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.StoreMapper;
import org.stockify.model.repository.StoreRepository;

/**
 * Service class responsible for managing store-related operations.
 * Provides functionality to create, update, retrieve, and delete stores,
 * including support for full and partial updates and paginated results.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreMapper storeMapper;

    /**
     * Retrieves a paginated list of all stores.
     *
     * @param pageable pagination and sorting parameters
     * @return a {@link Page} of {@link StoreResponse} representing the stores
     */
    public Page<StoreResponse> findAll(Pageable pageable) {
        Page<StoreEntity> stores = storeRepository.findAll(pageable);
        return stores.map(storeMapper::toResponse);
    }

    /**
     * Retrieves a store by its ID.
     *
     * @param id the ID of the store to retrieve
     * @return a {@link StoreResponse} containing the store's data
     * @throws NotFoundException if no store is found with the given ID
     */
    public StoreResponse findById(Long id) {
        StoreEntity store = getStoreById(id);
        return storeMapper.toResponse(store);
    }

    /**
     * Saves a new store in the database.
     *
     * @param request the DTO containing the store data to create
     * @return a {@link StoreResponse} representing the newly created store
     */
    public StoreResponse save(StoreRequest request) {
        if(storeRepository.existsById(1L)){
            throw new DuplicatedUniqueConstraintException("Ya existe una store.");
        }
        StoreEntity store = storeMapper.toEntity(request);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    /**
     * Updates all fields of an existing store.
     *
     * @param id      the ID of the store to update
     * @param request the DTO containing the updated store data
     * @return a {@link StoreResponse} with the updated store information
     * @throws NotFoundException if the store with the given ID does not exist
     */
    public StoreResponse update(Long id, StoreRequest request) {
        StoreEntity store = getStoreById(id);
        storeMapper.updateEntityFromRequest(request, store);
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    /**
     * Applies a partial update (patch) to an existing store.
     * Only non-null fields in the request will be updated.
     *
     * @param id      the ID of the store to update
     * @param request the DTO containing the fields to update
     * @return a {@link StoreResponse} representing the updated store
     * @throws NotFoundException if the store with the given ID does not exist
     */
    public StoreResponse patch(Long id, StoreRequest request) {
        StoreEntity store = getStoreById(id);
        storeMapper.patchEntityFromRequest(request, store);
        return storeMapper.toResponse(storeRepository.save(store));
    }

    /**
     * Deletes a store by its ID.
     *
     * @param id the ID of the store to delete
     */
    public void deleteById(Long id) {
        storeRepository.deleteById(id);
    }

    /**
     * Retrieves a {@link StoreEntity} by its ID or throws an exception if not found.
     * This method is used internally for validation or data access.
     *
     * @param id the ID of the store to retrieve
     * @return the {@link StoreEntity} instance
     * @throws NotFoundException if the store does not exist
     */
    public StoreEntity getStoreById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store not found with id: " + id));
    }

    public boolean existsById(Long id){
        return storeRepository.existsById(id);
    }

    /**
     * Ensures that a singleton store with ID=1 exists. If it doesn't, it will be created
     * with empty values for storeName, address, and city to allow later updates via PUT.
     */
    public void ensureSingletonDefault() {
        if (!storeRepository.existsById(1L)) {
            StoreEntity entity = new StoreEntity();
            entity.setId(1L);
            entity.setStoreName("");
            entity.setAddress("");
            entity.setCity("");
            storeRepository.save(entity);
        }
    }

    /**
     * Replaces the home carousel items for the singleton store.
     *
     * @param id    store id
     * @param items list with url and title
     * @return updated StoreResponse
     */
    public StoreResponse updateCarousel(Long id, java.util.List<HomeCarouselItem> items) {
        StoreEntity store = getStoreById(id);
        java.util.List<org.stockify.model.entity.CarouselItem> mapped =
                items == null ? java.util.List.of() :
                        items.stream()
                                .map(storeMapper::toEmbeddable)
                                .toList();
        store.setHomeCarousel(new java.util.ArrayList<>(mapped));
        store = storeRepository.save(store);
        return storeMapper.toResponse(store);
    }

    /**
     * Retrieves only the home carousel items for a store.
     *
     * @param id store id
     * @return list of carousel items (url, title, href)
     */
    public java.util.List<HomeCarouselItem> getCarousel(Long id) {
        StoreEntity store = getStoreById(id);
        return store.getHomeCarousel() == null ? java.util.List.of() :
                store.getHomeCarousel().stream()
                        .map(storeMapper::toDto)
                        .toList();
    }
}
