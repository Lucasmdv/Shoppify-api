package org.stockify.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.UriComponentsBuilder;
import org.stockify.dto.request.enviopack.CotizacionRequestDTO;
import org.stockify.dto.response.CotizacionResponseDTO;
import org.stockify.dto.response.TokenResponse;

import java.util.List;
import java.util.Map;

@Service
public class EnvioPackService {

    private static final String AUTH_URL = "https://api.enviopack.com/auth";
    private final String QUOTE_URL = "https://api.enviopack.com/cotizar/costo";

    private String token;
    private long expiresAt;

    @Value("${enviopack.api-key}")
    private String apiKey;

    @Value("${enviopack.secret}")
    private String secretKey;

    public synchronized String getAccessToken() {
        if (token == null || isExpired()) {
            refreshToken();
        }
        return token;
    }

    private boolean isExpired() {
        return System.currentTimeMillis() >= expiresAt;
    }

    private void refreshToken() {
        TokenResponse response = requestNewToken();

        this.token = response.token();
        this.expiresAt = System.currentTimeMillis()
                + (response.expiresIn() - 60) * 1000;

        System.out.println("EnvioPack token refreshed");
    }

    private TokenResponse requestNewToken() {

        System.out.println("apiKey=" + apiKey);
        System.out.println("secretKey=" + secretKey);

        if (apiKey == null || secretKey == null) {
            throw new IllegalStateException(
                    "ENVIOPACK_AK / ENVIOPACK_SK not configured");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
                "api-key", apiKey,
                "secret-key", secretKey);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                AUTH_URL,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                });

        Map<String, Object> bodyResponse = response.getBody();

        System.out.println("Enviopack Auth Response Body: " + bodyResponse);

        if (bodyResponse == null) {
            throw new RuntimeException("Empty EnvioPack auth response");
        }

        String tokenVal = null;
        if (bodyResponse.get("token") != null) {
            tokenVal = bodyResponse.get("token").toString();
        } else if (bodyResponse.get("access_token") != null) {
            tokenVal = bodyResponse.get("access_token").toString();
        }

        if (tokenVal == null) {
            throw new RuntimeException("Token not found in response: " + bodyResponse);
        }

        long expiresIn = 86400; // Default 24h
        if (bodyResponse.get("expires_in") != null) {
            try {
                expiresIn = Long.parseLong(bodyResponse.get("expires_in").toString());
            } catch (NumberFormatException e) {
                System.err.println("Invalid expires_in format: " + bodyResponse.get("expires_in"));
            }
        }

        return new TokenResponse(tokenVal, expiresIn);
    }

    public List<CotizacionResponseDTO> getCotizacion(CotizacionRequestDTO requestDTO) {
        String token = getAccessToken();

        // Construir URL con query params
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(QUOTE_URL)
                .queryParam("access_token", token)
                .queryParam("provincia", requestDTO.getProvincia())
                .queryParam("codigo_postal", requestDTO.getCodigoPostal())
                .queryParam("peso", requestDTO.getPeso());

        if (requestDTO.getVolumen() != null)
            builder.queryParam("volumen", requestDTO.getVolumen());
        if (requestDTO.getBultos() != null)
            builder.queryParam("bultos", requestDTO.getBultos());
        if (requestDTO.getPaquetes() != null)
            builder.queryParam("paquetes", requestDTO.getPaquetes());
        if (requestDTO.getCorreo() != null)
            builder.queryParam("correo", requestDTO.getCorreo());
        if (requestDTO.getDespacho() != null)
            builder.queryParam("despacho", requestDTO.getDespacho());
        if (requestDTO.getModalidad() != null)
            builder.queryParam("modalidad", requestDTO.getModalidad());
        if (requestDTO.getServicio() != null)
            builder.queryParam("servicio", requestDTO.getServicio());
        if (requestDTO.getDireccionEnvio() != null)
            builder.queryParam("direccion_envio", requestDTO.getDireccionEnvio());
        if (requestDTO.getOrdenColumna() != null)
            builder.queryParam("orden_columna", requestDTO.getOrdenColumna());
        if (requestDTO.getOrdenSentido() != null)
            builder.queryParam("orden_sentido", requestDTO.getOrdenSentido());
        if (requestDTO.getEmpresa() != null)
            builder.queryParam("empresa", requestDTO.getEmpresa());

        RestTemplate restTemplate = new RestTemplate();

        // Hacer la llamada GET
        ResponseEntity<List<CotizacionResponseDTO>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CotizacionResponseDTO>>() {}
        );

        return response.getBody();
    }
}
