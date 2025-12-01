package org.stockify.controller.store;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.store.StoreRequest;
import org.stockify.dto.response.StoreResponse;
import org.stockify.dto.shared.HomeCarouselItem;
import org.stockify.model.assembler.StoreModelAssembler;
import org.stockify.model.service.HomeCarouselService;
import org.stockify.model.service.StoreService;

import java.net.URI;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Tag(name = "Stores", description = "Operations related to store management")
@SecurityRequirement(name = "bearerAuth")
public class StoreController {

        private static final long STORE_ID = 1L;

        private final StoreService storeService;
        private final StoreModelAssembler storeModelAssembler;
        private final HomeCarouselService homeCarouselService;

        @Operation(summary = "Get singleton store")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Store found"),
                        @ApiResponse(responseCode = "404", description = "Store not found")
        })
        @GetMapping("/singleton")
        public ResponseEntity<EntityModel<StoreResponse>> getStore() {
                StoreResponse store = storeService.findById(STORE_ID);
                return ResponseEntity.ok(storeModelAssembler.toModel(store));
        }

        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Store updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Store not found"),
                        @ApiResponse(responseCode = "400", description = "Validation error")
        })
        @PutMapping
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WRITE')")
        public ResponseEntity<EntityModel<StoreResponse>> updateStore(
                        @Valid @RequestBody StoreRequest request) {
                StoreResponse store = storeService.update(STORE_ID, request);
                return ResponseEntity.ok(storeModelAssembler.toModel(store));
        }

        @Operation(
                summary = "Replace home carousel items",
                description = "Replaces the entire homeCarousel list (url, title) for the singleton store",
                security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carousel updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Store not found"),
                        @ApiResponse(responseCode = "400", description = "Validation error")
        })
        @PutMapping("/carousel")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WRITE')")
        public ResponseEntity<EntityModel<StoreResponse>> replaceCarousel(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of carousel items to set (url, title)")
                        @RequestBody java.util.List<HomeCarouselItem> items) {
                StoreResponse store = homeCarouselService.replaceCarousel(STORE_ID, items);
                return ResponseEntity.ok(storeModelAssembler.toModel(store));
        }

        @Operation(
                summary = "Get home carousel",
                description = "Returns only the homeCarousel array for the singleton store",
                security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carousel retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Store not found")
        })
        @GetMapping("/carousel")
        public ResponseEntity<java.util.List<HomeCarouselItem>> getCarousel() {
                return ResponseEntity.ok(homeCarouselService.listCarousel(STORE_ID));
        }

        @Operation(summary = "Get carousel item by id")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carousel item found"),
                        @ApiResponse(responseCode = "404", description = "Carousel item not found")
        })
        @GetMapping("/carousel/{itemId}")
        public ResponseEntity<HomeCarouselItem> getCarouselItem(
                        @Parameter(description = "Carousel identifier") @PathVariable Long itemId) {
                return ResponseEntity.ok(homeCarouselService.findCarouselItem(STORE_ID, itemId));
        }

        @Operation(summary = "Create carousel item")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Carousel item created"),
                        @ApiResponse(responseCode = "400", description = "Validation error")
        })
        @PostMapping("/carousel")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WRITE')")
        public ResponseEntity<HomeCarouselItem> createCarouselItem(
                        @RequestBody HomeCarouselItem item) {
                HomeCarouselItem created = homeCarouselService.createCarouselItem(STORE_ID, item);
                URI location = URI.create("/stores/carousel/" + created.id());
                return ResponseEntity.created(location).body(created);
        }

        @Operation(summary = "Update carousel item")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Carousel item updated"),
                        @ApiResponse(responseCode = "404", description = "Carousel item not found")
        })
        @PutMapping("/carousel/{itemId}")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WRITE')")
        public ResponseEntity<HomeCarouselItem> updateCarouselItem(
                        @Parameter(description = "Carousel identifier") @PathVariable Long itemId,
                        @RequestBody HomeCarouselItem item) {
                return ResponseEntity.ok(homeCarouselService.updateCarouselItem(STORE_ID, itemId, item));
        }

        @Operation(summary = "Delete carousel item")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Carousel item deleted"),
                        @ApiResponse(responseCode = "404", description = "Carousel item not found")
        })
        @DeleteMapping("/carousel/{itemId}")
        @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DELETE')")
        public ResponseEntity<Void> deleteCarouselItem(
                        @Parameter(description = "Carousel identifier") @PathVariable Long itemId) {
                homeCarouselService.deleteCarouselItem(STORE_ID, itemId);
                return ResponseEntity.noContent().build();
        }
}
