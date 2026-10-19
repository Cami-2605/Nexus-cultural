package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.ArticuloEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataArticuloRepository;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class ArticuloRepositoryPostgresAdapter implements ArticuloRepository {

    private final SpringDataArticuloRepository springDataArticuloRepository;
    private final ArticuloMapper articuloMapper;

    @Override
    public Articulo guardar(Articulo articulo) {
        ArticuloEntity entity = articuloMapper.toEntity(articulo);
        ArticuloEntity savedEntity = springDataArticuloRepository.save(entity);
        return articuloMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Articulo> buscarPorId(UUID id) {
        return springDataArticuloRepository.findById(id)
                .map(articuloMapper::toDomain);
    }

    @Override
    public void eliminar(UUID id) {
        springDataArticuloRepository.deleteById(id);
    }

    @Override
    public List<Articulo> listarDisponibles() {
        return springDataArticuloRepository.findByEstado(EstadoArticulo.DISPONIBLE.name())
                .stream()
                .map(articuloMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Articulo> buscarPorTitulo(String query) {
        return springDataArticuloRepository.findByEstadoAndTituloContainingIgnoreCase(EstadoArticulo.DISPONIBLE.name(), query)
                .stream()
                .map(articuloMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Articulo> buscarPorGrupo(Grupo grupo) {
        String grupoNombre = grupo != null ? grupo.name() : null;
        return springDataArticuloRepository.findByEstadoAndGrupo(EstadoArticulo.DISPONIBLE.name(), grupoNombre)
                .stream()
                .map(articuloMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Articulo> listarPorArtesano(UUID artesanoId) {
        return springDataArticuloRepository.findByArtesanoId(artesanoId)
                .stream()
                .map(articuloMapper::toDomain)
                .collect(Collectors.toList());
    }
}