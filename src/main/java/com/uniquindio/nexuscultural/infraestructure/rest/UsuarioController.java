package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.request.ValidarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.application.usecase.SolicitarOtpUseCase;
import com.uniquindio.nexuscultural.application.usecase.ValidarOtpUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final SolicitarOtpUseCase solicitarOtpUseCase;
    private final ValidarOtpUseCase validarOtpUseCase;

    public UsuarioController(SolicitarOtpUseCase solicitarOtpUseCase, ValidarOtpUseCase validarOtpUseCase) {
        this.solicitarOtpUseCase = solicitarOtpUseCase;
        this.validarOtpUseCase = validarOtpUseCase;
    }

    @PostMapping("/solicitar")
    public ResponseEntity<MensajeRespuestaResponse> solicitarCodigoOTP(@Valid @RequestBody SolicitarOtpRequest request) {
        MensajeRespuestaResponse respuesta = solicitarOtpUseCase.ejecutar(request);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/verificar")
    public ResponseEntity<MensajeRespuestaResponse> validarCodigoOTP(@Valid @RequestBody ValidarOtpRequest request) {
        MensajeRespuestaResponse respuesta = validarOtpUseCase.ejecutar(request);
        return ResponseEntity.ok(respuesta);
    }
}
