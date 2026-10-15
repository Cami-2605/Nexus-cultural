package com.uniquindio.nexuscultural.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (Registro y Autenticación OTP)
                        .requestMatchers("/api/usuarios/solicitar", "/api/usuarios/verificar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/administradores", "/api/artesanos", "/api/compradores").permitAll()

                        // Endpoints protegidos por rol
                        .requestMatchers("/api/administradores/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/artesanos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers("/api/compradores/**").hasAnyRole("COMPRADOR", "ADMINISTRADOR")

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}