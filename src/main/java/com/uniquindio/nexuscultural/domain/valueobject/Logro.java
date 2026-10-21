package com.uniquindio.nexuscultural.domain.valueobject;

/**
 * Enum que representa los logros que un artesano puede obtener
 * mediante su actividad en la plataforma (gamificación).
 */
public enum Logro {
    PRIMER_ARTICULO("Primer artículo", "Publicaste tu primer artículo en el catálogo."),
    CINCO_ARTICULOS("Cinco artículos", "Tienes 5 artículos publicados en el catálogo."),
    DIEZ_ARTICULOS("Diez artículos", "Tienes 10 artículos publicados en el catálogo."),
    PRIMER_VENTA("Primera venta", "Realizaste tu primera venta."),
    CINCO_VENTAS("Cinco ventas", "Completaste 5 ventas exitosas.");

    private final String nombre;
    private final String descripcion;

    Logro(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
