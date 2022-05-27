package com.exemplo.ejle_commerce.model;

public enum StatusPedido {

    PENDENTE,
    APROVADO,
    CANCELADO;

    public static String getStatus(StatusPedido status) {
        String statusPedido = null;

        switch (status) {
            case PENDENTE:
                statusPedido = "Pendente";
                break;
            case APROVADO:
                statusPedido = "Aprovado";
                break;
            case CANCELADO:
                statusPedido = "Cancelado";
                break;
        }

        return statusPedido;
    }

}
