package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Logro;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Servicio de aplicación para la evaluación y otorgamiento de logros de gamificación.
 * Se invoca después de publicar un artículo para evaluar si el artesano merece un nuevo logro.
 */
@Service
public class LogroService {

    private final ArtesanoRepository artesanoRepository;

    public LogroService(ArtesanoRepository artesanoRepository) {
        this.artesanoRepository = artesanoRepository;
    }

    /**
     * Evalúa y otorga logros de publicación al artesano según el total de artículos
     * que tiene publicados. Debe llamarse después de guardar el nuevo artículo.
     *
     * @param artesanoId     ID del artesano a evaluar.
     * @param totalArticulos total de artículos del artesano (incluye el recién publicado).
     * @return el artesano actualizado con los logros otorgados.
     */
    public Artesano evaluarLogrosPublicacion(UUID artesanoId, int totalArticulos) {
        Artesano artesano = artesanoRepository.buscarPorId(artesanoId)
                .orElseThrow(() -> new ReglaDominioException("Artesano no encontrado."));

        if (totalArticulos >= 1)  artesano.otorgarLogro(Logro.PRIMER_ARTICULO);
        if (totalArticulos >= 5)  artesano.otorgarLogro(Logro.CINCO_ARTICULOS);
        if (totalArticulos >= 10) artesano.otorgarLogro(Logro.DIEZ_ARTICULOS);

        return artesanoRepository.guardar(artesano);
    }
}
