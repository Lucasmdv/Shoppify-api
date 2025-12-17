package org.stockify.dto.request.enviopack;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para solicitar la cotización de un envío en EnvíoPack")
public class CotizacionRequestDTO {

    @NotNull
    @Schema(description = "Provincia destino (ID ISO_3166-2:AR sin 'AR-')", example = "C")
    private String provincia;

    @NotNull
    @JsonProperty("codigo_postal")
    @Schema(description = "Código postal del destino", example = "1405")
    private Integer codigoPostal;

    @NotNull
    @Min(0)
    @Schema(description = "Peso del paquete en kilogramos", example = "1.5")
    private Double peso;

    @Min(0)
    @Schema(description = "Volumen del paquete en metros cúbicos", example = "0.03")
    private Double volumen;

    @Min(1)
    @Schema(description = "Cantidad de bultos o paquetes", example = "1")
    private Integer bultos;

    @Schema(description = "Dimensiones del paquete en formato LxAxH (cm)", example = "20x10x5")
    private String paquetes;

    @Schema(description = "Correo preferido (opcional)", example = "oca")
    private String correo;

    @Schema(description = "Tipo de despacho: a domicilio o a sucursal", example = "domicilio")
    private String despacho;

    @Schema(description = "Modalidad del envío: express, estándar, etc.", example = "express")
    private String modalidad;

    @Schema(description = "Servicio adicional, si aplica", example = "seguros")
    private String servicio;

    @JsonProperty("direccion_envio")
    @Schema(description = "ID de la dirección de origen registrada en EnvíoPack", example = "1234")
    private String direccionEnvio;

    @JsonProperty("orden_columna")
    @Schema(description = "Columna para ordenar resultados: valor, horas_entrega, cumplimiento, anomalos", example = "valor")
    private String ordenColumna;

    @JsonProperty("orden_sentido")
    @Schema(description = "Sentido de ordenamiento: asc o desc", example = "asc")
    private String ordenSentido;

    @Schema(description = "Empresa de logística, si aplica", example = "EnvioPack")
    private String empresa;
}
