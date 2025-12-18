package org.stockify.model.enums;

public enum OrderStatus {
    PENDING("Pendiente"),
    PROCESSING("Procesando"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado"),
    RETURNED("Devuelto");

    private final String translation;

    OrderStatus(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}