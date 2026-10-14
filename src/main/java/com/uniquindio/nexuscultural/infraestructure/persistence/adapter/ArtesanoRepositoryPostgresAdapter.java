package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.ArtesanoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataArtesanoRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class ArtesanoRepositoryPostgresAdapter implements ArtesanoRepository {

    private final SpringDataArtesanoRepository springDataRepository;

    public ArtesanoRepositoryPostgresAdapter(SpringDataArtesanoRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }


    @Override
    public Artesano guardar(Artesano artesano) {
        System.out.println("🔥🔥🔥 INGRESÓ AL ADAPTADOR DE POSTGRESQL 🔥🔥🔥");
        ArtesanoEntity entity = toEntity(artesano);
        ArtesanoEntity guardado = springDataRepository.save(entity);
        return toDomain(guardado);
    }

    @Override
    public Optional<Artesano> buscarPorId(UUID id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Artesano> buscarPorEmail(Email email) {
        return springDataRepository.findByEmail(email.valor()).map(this::toDomain);
    }

    private ArtesanoEntity toEntity(Artesano artesano) {
        ArtesanoEntity entity = new ArtesanoEntity();
        entity.setRol(RolUsuario.ARTESANO);
        entity.setId(artesano.getId());
        entity.setNombreCompleto(artesano.getNombreCompleto());
        entity.setEmail(artesano.getEmail().valor());
        entity.setFechaRegistro(artesano.getFechaRegistro()); // <-- Mapeo de fecha de registro

        // Mapeo desde TrayectoriaCultural (Value Object)
        if (artesano.getTrayectoriaCultural() != null) {
            entity.setAnosExperiencia(artesano.getTrayectoriaCultural().anosExperiencia());
            entity.setReconocimientos(artesano.getTrayectoriaCultural().reconocimientos());
        }

        // Mapeo desde UbicacionGeografica (Value Object)
        if (artesano.getUbicacionGeografica() != null) {
            entity.setDepartamento(artesano.getUbicacionGeografica().departamento());
            entity.setMunicipio(artesano.getUbicacionGeografica().municipio());
            entity.setDireccion(artesano.getUbicacionGeografica().direccion());
        }

        entity.setActivo(artesano.isActivo());
        return entity;
    }

    private Artesano toDomain(ArtesanoEntity entity) {
        // Reconstrucción del Value Object TrayectoriaCultural
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(
                entity.getAnosExperiencia(),
                entity.getReconocimientos()
        );

        // Reconstrucción del Value Object UbicacionGeografica
        UbicacionGeografica ubicacion = new UbicacionGeografica(
                entity.getDepartamento(),
                entity.getMunicipio(),
                entity.getDireccion()
        );

        // Reconstrucción de Artesano usando su Builder
        return Artesano.builder()
                .id(entity.getId())
                .nombreCompleto(entity.getNombreCompleto())
                .email(new Email(entity.getEmail()))
                .fechaRegistro(entity.getFechaRegistro()) // <-- Mapeo en el Builder
                .trayectoriaCultural(trayectoria)
                .ubicacionGeografica(ubicacion)
                .activo(entity.isActivo())
                .build();
    }
}