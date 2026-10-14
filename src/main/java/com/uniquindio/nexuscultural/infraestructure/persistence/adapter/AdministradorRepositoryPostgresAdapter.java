package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.AdministradorEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataAdministradorRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class AdministradorRepositoryPostgresAdapter implements AdministradorRepository {

    private final SpringDataAdministradorRepository springDataRepository;

    public AdministradorRepositoryPostgresAdapter(SpringDataAdministradorRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Administrador guardar(Administrador administrador) {
        AdministradorEntity entity = toEntity(administrador);
        AdministradorEntity guardado = springDataRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<Administrador> buscarPorId(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Administrador> buscarPorEmail(Email email) {
        return springDataRepository.findByEmail(email.valor()).map(this::toDomain);
    }

    private AdministradorEntity toEntity(Administrador admin) {
        AdministradorEntity entity = new AdministradorEntity();
        entity.setRol(RolUsuario.ADMIN);
        entity.setId(admin.getId());
        entity.setNombreCompleto(admin.getNombreCompleto());
        entity.setEmail(admin.getEmail().valor());
        entity.setFechaRegistro(admin.getFechaRegistro());
        entity.setNivelAcceso(admin.getNivelAcceso());
        return entity;
    }

    private Administrador toDomain(AdministradorEntity entity) {
        return Administrador.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro())
                .nivelAcceso(entity.getNivelAcceso())
                .build();
    }
}
