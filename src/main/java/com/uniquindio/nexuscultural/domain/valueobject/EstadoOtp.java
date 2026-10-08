package com.uniquindio.nexuscultural.domain.valueobject;

//Modela los estados del código para validar la identidad de usuarios en el login
public enum EstadoOtp {
    PENDIENTE("Código activo para su uso"),
    USADO("Código verificado exitosamente"),
    EXPIRADO("Código fuera del tiempo límite");

    private final String descripcion;

    EstadoOtp(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}