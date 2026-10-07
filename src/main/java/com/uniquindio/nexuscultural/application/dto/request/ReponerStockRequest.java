package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;

/**
 * Mapea a Articulo.reponerStock().
 */
public record ReponerStockRequest(
        // @Min en vez de @NotBlank: @NotBlank solo funciona en String, no en primitivos
        @Min(value = 1, message = "Ingrese una cantidad mayor o igual a 1")
        int stock
) {}
