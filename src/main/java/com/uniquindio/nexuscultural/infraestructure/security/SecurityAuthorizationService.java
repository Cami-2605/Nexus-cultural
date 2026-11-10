package com.uniquindio.nexuscultural.infraestructure.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityAuthorizationService {

    /**
     * Valida que el usuario autenticado sea el dueño del recurso especificado por recursoUsuarioId
     * o que cuente con el rol ADMINISTRADOR. Si no cumple, lanza AccessDeniedException (HTTP 403).
     */
    public void validarAccesoRecursoOAdmin(UUID recursoUsuarioId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("No hay una sesión autenticada válida.");
        }

        // Si es ADMINISTRADOR, se permite el acceso
        boolean esAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equalsIgnoreCase("ROLE_ADMINISTRADOR")
                        || rol.equalsIgnoreCase("ROLE_ADMIN")
                        || rol.equalsIgnoreCase("ADMINISTRADOR")
                        || rol.equalsIgnoreCase("ADMIN"));
        if (esAdmin) {
            return;
        }

        // Se obtiene el ID del usuario desde las credenciales / principal del JWT
        Object credentials = auth.getCredentials();
        String usuarioAutenticadoIdStr = (credentials != null) ? credentials.toString() : null;

        if (usuarioAutenticadoIdStr == null || usuarioAutenticadoIdStr.isBlank()) {
            // Intentar por principal si credentials no vino
            Object principal = auth.getPrincipal();
            if (principal != null) {
                usuarioAutenticadoIdStr = principal.toString();
            }
        }

        if (usuarioAutenticadoIdStr != null && recursoUsuarioId != null) {
            try {
                UUID authId = UUID.fromString(usuarioAutenticadoIdStr);
                if (authId.equals(recursoUsuarioId)) {
                    return; // Es el dueño legítimo
                }
            } catch (IllegalArgumentException ignored) {
                // Si el principal fuera el email, comparar con null o no coincide
            }
        }

        throw new AccessDeniedException("Acceso denegado: No tiene permisos para consultar o modificar este recurso ajeno.");
    }

    /**
     * Retorna el UUID del usuario autenticado en la petición actual.
     */
    public UUID obtenerUsuarioAutenticadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) {
            return null;
        }
        try {
            return UUID.fromString(auth.getCredentials().toString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica si el usuario actual es ADMINISTRADOR.
     */
    public boolean esAdministrador() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equalsIgnoreCase("ROLE_ADMINISTRADOR") || rol.equalsIgnoreCase("ROLE_ADMIN"));
    }
}

