package org.stockify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WishlistResponse {
    private Long id;
    private Long userId;
    private Set<WishlistProductResponse> products;

}
