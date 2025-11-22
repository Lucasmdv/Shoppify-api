package org.stockify.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MercadoPagoPreferenceResponse {

    @Schema(description = "Mercado Pago preference identifier")
    private String preferenceId;

    @Schema(description = "Init point URL for redirecting the user")
    private String initPoint;

    @Schema(description = "Sandbox init point URL")
    private String sandboxInitPoint;
}
