package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.adapter.CodigoOtpRepositoryPostgresAdapter;
import com.uniquindio.nexuscultural.infraestructure.security.JwtTokenProviderAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ValidarOtpUseCase {

    private static final Map<String, Integer> INTENTOS_POR_EMAIL = new ConcurrentHashMap<>();

    public static void reiniciarIntentos(String email) {
        if (email != null) {
            INTENTOS_POR_EMAIL.remove(email.toLowerCase().trim());
        }
    }

    private final CodigoOtpRepository otpRepository;
    private final AdministradorRepository administradorRepository;
    private final ArtesanoRepository artesanoRepository;
    private final CompradorRepository compradorRepository;
    private final JwtTokenProviderAdapter jwtTokenProviderAdapter;

    public ValidarOtpUseCase(CodigoOtpRepository otpRepository,
                             AdministradorRepository administradorRepository,
                             ArtesanoRepository artesanoRepository,
                             CompradorRepository compradorRepository,
                             JwtTokenProviderAdapter jwtTokenProviderAdapter) {
        this.otpRepository = otpRepository;
        this.administradorRepository = administradorRepository;
        this.artesanoRepository = artesanoRepository;
        this.compradorRepository = compradorRepository;
        this.jwtTokenProviderAdapter = jwtTokenProviderAdapter;
    }

    @Transactional
    public AuthTokenResponse ejecutar(String emailTexto, String codigoIngresado) {
        String keyEmail = emailTexto != null ? emailTexto.toLowerCase().trim() : "";
        int intentos = INTENTOS_POR_EMAIL.getOrDefault(keyEmail, 0);

        // Validar límite previo de 3 intentos fallidos
        if (intentos >= 3) {
            throw new ReglaDominioException("Ha superado el número máximo de intentos permitidos (3). El código fue bloqueado; solicite uno nuevo.");
        }

        CodigoOtp otpActual = otpRepository.buscarUltimoPorEmail(emailTexto)
                .orElseThrow(() -> new ReglaDominioException("No se encontró ningún código OTP para el correo especificado."));

        if (otpActual.intentosFallidos() >= 3 || otpActual.estado() == EstadoOtp.EXPIRADO) {
            throw new ReglaDominioException("Ha superado el número máximo de intentos permitidos (3). El código fue bloqueado; solicite uno nuevo.");
        }

        if (otpActual.estaExpirado()) {
            throw new ReglaDominioException("El código OTP ha expirado. Por favor solicite uno nuevo.");
        }

        if (otpActual.estado() == EstadoOtp.USADO) {
            throw new ReglaDominioException("El código OTP ya fue utilizado.");
        }

        // Comparación segura del hash SHA-256
        String hashIngresado = CodigoOtpRepositoryPostgresAdapter.hashearCodigo(codigoIngresado);
        if (!otpActual.codigo().equalsIgnoreCase(hashIngresado)) {
            intentos++;
            INTENTOS_POR_EMAIL.put(keyEmail, intentos);

            CodigoOtp otpConFallo = otpActual.incrementarIntentos();
            otpRepository.actualizar(emailTexto, otpConFallo);

            int intentosRestantes = 3 - intentos;
            if (intentosRestantes <= 0) {
                throw new ReglaDominioException("Código incorrecto. Ha superado el límite de 3 intentos y el código ha sido bloqueado.");
            } else {
                throw new ReglaDominioException("El código OTP es incorrecto. Intentos restantes: " + intentosRestantes);
            }
        }

        // Limpiar intentos fallidos al tener éxito
        INTENTOS_POR_EMAIL.remove(keyEmail);

        // Marcar OTP como usado
        CodigoOtp otpUsado = otpActual.marcarComoUsado();
        otpRepository.actualizar(emailTexto, otpUsado);

        // Convertir String a Value Object Email para consultar los repositorios
        Email emailVO = new Email(emailTexto);

        UUID usuarioId;
        String rol;
        String nivelAcceso = null;
        String nombre = null;

        var adminOpt = administradorRepository.buscarPorEmail(emailVO);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            usuarioId = admin.getId();
            nombre = admin.getNombreCompleto();
            rol = "ADMINISTRADOR";
            nivelAcceso = admin.getNivelAcceso() != null ? admin.getNivelAcceso().name() : "SUPER_ADMIN";
        } else if (artesanoRepository.buscarPorEmail(emailVO).isPresent()) {
            var artesano = artesanoRepository.buscarPorEmail(emailVO).get();
            usuarioId = artesano.getId();
            nombre = artesano.getNombreCompleto();
            rol = "ARTESANO";
        } else if (compradorRepository.buscarPorEmail(emailVO).isPresent()) {
            var comprador = compradorRepository.buscarPorEmail(emailVO).get();
            usuarioId = comprador.getId();
            nombre = comprador.getNombreCompleto();
            rol = "COMPRADOR";
        } else {
            throw new ReglaDominioException("El correo no está asociado a ningún usuario registrado.");
        }

        // Generar y retornar el token JWT
        return jwtTokenProviderAdapter.generarToken(usuarioId, emailTexto, rol, nivelAcceso, nombre);
    }
}