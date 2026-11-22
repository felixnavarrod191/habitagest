package com.habitagest.pqr;

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

/** El contrato HTTP de PQR y comunicados: estados y cuerpo de error {codigo, mensaje}. */
@WebMvcTest({ControladorPqr.class, ControladorComunicados.class})
class ControladorPqrTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioPqr servicio;

    @MockBean
    private ServicioComunicados comunicados;

    @Test
    void radicarUnaPqrResponde201ConSuEstado() throws Exception {
        when(servicio.radicar(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new Pqr("PQR-0001", "r1", "Gotera", "Hay una gotera.", LocalDateTime.of(2026, 11, 19, 10, 0)));

        mvc.perform(post("/pqr").contentType(MediaType.APPLICATION_JSON)
                .content("{\"residenteId\":\"r1\",\"asunto\":\"Gotera\",\"descripcion\":\"Hay una gotera.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RADICADA"))
                .andExpect(jsonPath("$.numero").value("PQR-0001"));
    }

    @Test
    void unSaltoDeEstadoResponde409() throws Exception {
        when(servicio.cambiarEstado(anyString(), anyString(), anyString()))
                .thenThrow(ErrorDeNegocio.reglaDeNegocio("La PQR está RADICADA y solo puede pasar a EN_REVISION."));

        mvc.perform(post("/pqr/p1/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"RESUELTA\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("E409"));
    }

    @Test
    void radicarSinDescripcionResponde400() throws Exception {
        mvc.perform(post("/pqr").contentType(MediaType.APPLICATION_JSON)
                .content("{\"residenteId\":\"r1\",\"asunto\":\"Gotera\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }

    @Test
    void publicarUnComunicadoSinVigenciaResponde400() throws Exception {
        mvc.perform(post("/comunicados").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Aviso\",\"texto\":\"Texto\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }
}
