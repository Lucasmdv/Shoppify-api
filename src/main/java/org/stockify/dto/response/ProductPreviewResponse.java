package org.stockify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPreviewResponse {
    private Map<String, String> data;
    private Map<String, String> errors;
}
