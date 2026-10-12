package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record RegistrarArtesanoRequest(
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El formato de correo electrónico no es válido")
        @Size(min = 3, max = 100, message = "El correo debe tener entre 3 y 100 caracteres")
        String email,

        @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
        int anosExperiencia,

        @NotNull(message = "La lista de reconocimientos no puede ser nula (puede ser una lista vacía)")
        List<String> reconocimientos,

        @NotBlank(message = "El departamento es obligatorio")
        String departamento,

        @NotBlank(message = "El municipio es obligatorio")
        String municipio,

        @NotBlank(message = "La dirección es obligatoria")
        String direccion
) {}