package org.stockify.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistProductResponse {

    private Long id;
    private LocalDateTime createdAt;
    private ProductResponse product;
}
