package com.uniquindio.nexuscultural.infraestructure.config;

import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.AdministradorRepositoryEnMemoria;
import com.uniquindio.nexuscultural.infraestructure.persistence.ArticuloRepositoryEnMemoria;
import com.uniquindio.nexuscultural.infraestructure.persistence.ArtesanoRepositoryEnMemoria;
import com.uniquindio.nexuscultural.infraestructure.persistence.CarritoRepositoryEnMemoria;
import com.uniquindio.nexuscultural.infraestructure.persistence.CompradorRepositoryEnMemoria;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los adaptadores de persistencia en memoria como beans de Spring.
 * Cuando se migre a JPA, basta con reemplazar cada bean por su implementación JPA
 * sin tocar ninguna otra capa.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ArtesanoRepository artesanoRepository() {
        return new ArtesanoRepositoryEnMemoria();
    }

    @Bean
    public CompradorRepository compradorRepository() {
        return new CompradorRepositoryEnMemoria();
    }

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(AdministradorRepository.class)
    public AdministradorRepository administradorRepository() {
        return new AdministradorRepositoryEnMemoria();
    }

    @Bean
    public ArticuloRepository articuloRepository() {
        return new ArticuloRepositoryEnMemoria();
    }


    @Bean
    public CarritoRepository carritoRepository() {
        return new CarritoRepositoryEnMemoria();
    }
}
