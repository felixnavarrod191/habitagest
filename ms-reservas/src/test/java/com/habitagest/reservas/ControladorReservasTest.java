package com.habitagest.reservas;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** El contrato HTTP de reservas: estados y cuerpo de error {codigo, mensaje}. */
@WebMvcTest(ControladorReservas.class)
class ControladorReservasTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioReservas servicio;

    @Test
    void reservarEnUnHorarioLibreResponde201() throws Exception {
        when(servicio.reservar(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenReturn(new Reserva("z1", "a1", LocalDateTime.of(2026, 11, 21, 14, 0),
                        LocalDateTime.of(2026, 11, 21, 16, 0)));

        mvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content(
                "{\"zonaId\":\"z1\",\"apartamentoId\":\"a1\",\"inicio\":\"2026-11-21T14:00:00\",\"fin\":\"2026-11-21T16:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void unCruceDeHorarioResponde409() throws Exception {
        when(servicio.reservar(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class), anyString()))
                .thenThrow(ErrorDeNegocio.reglaDeNegocio("El horario ya está reservado en esa zona (cruce de horario)."));

        mvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content(
                "{\"zonaId\":\"z1\",\"apartamentoId\":\"a2\",\"inicio\":\"2026-11-21T15:00:00\",\"fin\":\"2026-11-21T17:00:00\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("E409"));
    }

    @Test
    void unaCancelacionSinSolicitanteResponde400() throws Exception {
        mvc.perform(post("/reservas/r1/cancelaciones").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }
}
