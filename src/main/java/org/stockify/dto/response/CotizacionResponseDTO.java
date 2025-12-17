package org.stockify.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response de cotización de envío según EnvíoPack")
public class CotizacionResponseDTO implements Serializable {

    @Schema(description = "Datos del correo/transportista", required = true)
    private CorreoDTO correo;

    @Schema(description = "Tipo de despacho (ej: domicilio o sucursal)", example = "domicilio")
    private String despacho;

    @Schema(description = "Modalidad del envío (ej: express, estándar)", example = "estandar")
    private String modalidad;

    @Schema(description = "Servicio adicional (si aplica)", example = "clasico")
    private String servicio;

    @JsonProperty("peso_desde")
    @Schema(description = "Peso mínimo para esta cotización", example = "0")
    private String pesoDesde;

    @JsonProperty("peso_hasta")
    @Schema(description = "Peso máximo para esta cotización", example = "30")
    private String pesoHasta;

    @Schema(description = "Valor estimado del envío en ARS", example = "3200")
    private String valor;

    @JsonProperty("horas_entrega")
    @Schema(description = "Horas estimadas de entrega", example = "72")
    private Integer horasEntrega;

    @Schema(description = "Cumplimiento de entregas (%)", example = "98")
    private Integer cumplimiento;

    @Schema(description = "Anómalos reportados", example = "3")
    private Integer anomalos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Objeto con datos del correo/transportista")
    public static class CorreoDTO implements Serializable {

        @Schema(description = "Identificador interno del correo/transportista", example = "oca")
        private String id;

        @Schema(description = "Nombre legible del correo/transportista", example = "OCA")
        private String nombre;
    }
}
