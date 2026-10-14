package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.infraestructure.security.JwtTokenProviderAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ValidarOtpUseCase {

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
        CodigoOtp otpActual = otpRepository.buscarUltimoPorEmail(emailTexto)
                .orElseThrow(() -> new ReglaDominioException("No se encontró ningún código OTP para el correo especificado."));

        if (!otpActual.esValido(codigoIngresado)) {
            throw new ReglaDominioException("El código OTP es incorrecto, ya fue utilizado o ha expirado.");
        }

        // Marcar OTP como usado
        CodigoOtp otpUsado = otpActual.marcarComoUsado();
        otpRepository.actualizar(emailTexto, otpUsado);

        // Convertir String a Value Object Email para consultar los repositorios
        Email emailVO = new Email(emailTexto);

        UUID usuarioId;
        String rol;

        if (administradorRepository.buscarPorEmail(emailVO).isPresent()) {
            usuarioId = administradorRepository.buscarPorEmail(emailVO).get().getId();
            rol = "ADMINISTRADOR";
        } else if (artesanoRepository.buscarPorEmail(emailVO).isPresent()) {
            usuarioId = artesanoRepository.buscarPorEmail(emailVO).get().getId();
            rol = "ARTESANO";
        } else if (compradorRepository.buscarPorEmail(emailVO).isPresent()) {
            usuarioId = compradorRepository.buscarPorEmail(emailVO).get().getId();
            rol = "COMPRADOR";
        } else {
            throw new ReglaDominioException("El correo no está asociado a ningún usuario registrado.");
        }

        // Generar y retornar el token JWT
        return jwtTokenProviderAdapter.generarToken(usuarioId, emailTexto, rol);
    }
}