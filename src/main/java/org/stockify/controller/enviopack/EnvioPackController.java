package org.stockify.controller.enviopack;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.enviopack.CotizacionRequestDTO;
import org.stockify.model.service.EnvioPackService;

import java.util.List;
import org.stockify.dto.response.CotizacionResponseDTO;

@RestController
@RequestMapping("/enviopack")
public class EnvioPackController {

    private final EnvioPackService envioPackService;

    public EnvioPackController(EnvioPackService envioPackService) {
        this.envioPackService = envioPackService;
    }

    @PostMapping("/auth")
    public ResponseEntity<String> getAccessToken() {
        return ResponseEntity.ok(envioPackService.getAccessToken());
    }

    @PostMapping("/cotizar")
    public ResponseEntity<List<CotizacionResponseDTO>> cotizar(@RequestBody CotizacionRequestDTO requestDTO) {
        List<CotizacionResponseDTO> cotizaciones = envioPackService.getCotizacion(requestDTO);
        return ResponseEntity.ok(cotizaciones);
    }
}
