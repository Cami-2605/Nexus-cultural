package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

/**
 * Mapea a Articulo.crear().
 * Contiene la información que el artesano debe ingresar cuando publica un producto.
 */
public record CrearArticuloRequest(

        @NotNull(message = "El ID del artesano es obligatorio")
        UUID artesanoId,

        @NotBlank(message = "El título del artículo es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción del artículo es obligatoria")
        String descripcion,

        // @Min en vez de @NotBlank: @NotBlank solo funciona en String, no en primitivos
        @Min(value = 1, message = "El stock debe ser al menos 1")
        int stock,

        @NotNull(message = "El grupo del artículo es obligatorio")
        Grupo grupo,

        // Mínimo 2 imágenes por regla de negocio (Articulo.MINIMO_IMAGENES = 2)
        @NotNull(message = "Debe incluir al menos 2 imágenes")
        @Size(min = 2, message = "Debe incluir al menos 2 imágenes")
        List<String> imagenes,

        // @NotNull en vez de @NotBlank: Precio es un objeto, no un String
        @NotNull(message = "El precio del artículo es obligatorio")
        Precio precio
) {}
