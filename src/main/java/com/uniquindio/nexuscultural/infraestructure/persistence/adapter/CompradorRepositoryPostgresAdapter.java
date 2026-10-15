package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CompradorEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataCompradorRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CompradorRepositoryPostgresAdapter implements CompradorRepository {

    private final SpringDataCompradorRepository springDataRepository;

    public CompradorRepositoryPostgresAdapter(SpringDataCompradorRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Comprador guardar(Comprador comprador) {
        CompradorEntity entity = toEntity(comprador);
        CompradorEntity guardado = springDataRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<Comprador> buscarPorId(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Comprador> buscarPorEmail(Email email) {
        return springDataRepository.findByEmail(email.valor()).map(this::toDomain);
    }

    private CompradorEntity toEntity(Comprador comprador) {
        CompradorEntity entity = new CompradorEntity();
        entity.setRol(RolUsuario.COMPRADOR);
        entity.setId(comprador.getId());
        entity.setNombreCompleto(comprador.getNombreCompleto());
        entity.setEmail(comprador.getEmail().valor());
        entity.setFechaRegistro(comprador.getFechaRegistro());

        if (comprador.getDireccionEntrega() != null) {
            entity.setDepartamento(comprador.getDireccionEntrega().departamento());
            entity.setMunicipio(comprador.getDireccionEntrega().municipio());
            entity.setDireccion(comprador.getDireccionEntrega().direccion());
        }

        entity.setFavoritos(comprador.getFavoritos());
        return entity;
    }

    private Comprador toDomain(CompradorEntity entity) {
        UbicacionGeografica direccionEntrega = new UbicacionGeografica(
                entity.getDepartamento(),
                entity.getMunicipio(),
                entity.getDireccion()
        );

        return Comprador.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro())
                .favoritos(entity.getFavoritos())
                .direccionEntrega(direccionEntrega)
                .build();
    }
}