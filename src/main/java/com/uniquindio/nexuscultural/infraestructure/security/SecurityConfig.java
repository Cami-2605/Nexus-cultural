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
                        // 1. Endpoints públicos (Registro, Autenticación OTP y Versión de App)
                        .requestMatchers("/api/usuarios/solicitar", "/api/usuarios/verificar").permitAll()
                        .requestMatchers("/api/usuarios/**", "/api/app-version", "/api/version").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/administradores", "/api/artesanos", "/api/compradores").permitAll()

                        // 2. Catálogo público (para que compradores y visitantes vean las obras)
                        .requestMatchers(HttpMethod.GET, "/api/catalogo/**", "/api/articulos/**").permitAll()

                        // 3. Almacenamiento en AWS S3 (Presigned URLs)
                        // Permite que artesanos autenticados soliciten la URL de subida
                        .requestMatchers("/api/v1/storage/**").permitAll()

                        // 4. Publicación y gestión de artículos
                        .requestMatchers(HttpMethod.POST, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/articulos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")

                        // 5. Endpoints protegidos por rol
                        .requestMatchers("/api/administradores", "/api/administradores/**").hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/artesanos/**").hasAnyRole("ARTESANO", "ADMINISTRADOR")
                        .requestMatchers("/api/compradores/**").hasAnyRole("COMPRADOR", "ADMINISTRADOR")

                        // 6. Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}