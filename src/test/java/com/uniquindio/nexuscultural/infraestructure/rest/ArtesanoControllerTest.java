package com.uniquindio.nexuscultural.infraestructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.usecase.RegistrarArtesanoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArtesanoMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtesanoController.class) //Levanta lo necesario para probar este controller
@Import(ArtesanoMapper.class) // Carga la implementación real de ArtesanoMapper en el contexto
class ArtesanoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegistrarArtesanoUseCase registrarArtesanoUseCase;

    //Aquí se estan llamando los test verificando que si responda
    @Test
    @DisplayName("POST /artesanos debe responder HTTP 200 OK y devolver el JSON del artesano creado")
    void debeRegistrarArtesanoViaHttp() throws Exception {
        // Arrange
        RegistrarArtesanoRequest request = new RegistrarArtesanoRequest(
                "Lucía Fernández",
                "lucia@artesanias.com",
                8,
                List.of("Feria Artesanal 2023"),
                "Quindío",
                "Salento",
                "Calle Real #2-15"
        );

        Artesano artesanoSimulado = Artesano.builder()
                .id(UUID.randomUUID())
                .nombreCompleto("Lucía Fernández")
                .email(new Email("lucia@artesanias.com"))
                .fechaRegistro(LocalDateTime.now())
                .trayectoriaCultural(new TrayectoriaCultural(8, List.of("Feria Artesanal 2023")))
                .ubicacionGeografica(new UbicacionGeografica("Quindío", "Salento", "Calle Real #2-15"))
                .activo(true)
                .build();

        when(registrarArtesanoUseCase.ejecutar(anyString(), anyString(), any(), any()))
                .thenReturn(artesanoSimulado);

        // Act & Assert
        mockMvc.perform(post("/api/artesanos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //.andExpect(status().isOk()) //Se usa para probar el 200
                .andExpect(status().isCreated()) //Para validar el estado 201
                .andExpect(jsonPath("$.nombreCompleto").value("Lucía Fernández"))
                .andExpect(jsonPath("$.email").value("lucia@artesanias.com"))
                .andExpect(jsonPath("$.activo").value(true));
    }
}