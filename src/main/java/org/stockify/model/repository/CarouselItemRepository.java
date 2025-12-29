package org.stockify.model.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.CarouselItem;

@Repository
public interface CarouselItemRepository extends JpaRepository<CarouselItem, Long> {

    List<CarouselItem> findAllByStoreId(Long storeId);

    Optional<CarouselItem> findByIdAndStoreId(Long id, Long storeId);
}
