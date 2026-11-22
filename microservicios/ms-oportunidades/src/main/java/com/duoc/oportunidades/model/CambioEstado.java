package com.duoc.oportunidades.model;

import jakarta.validation.constraints.NotBlank;

public class CambioEstado {

    @NotBlank(message = "El campo estado es obligatorio")
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
