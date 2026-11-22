package com.habitagest.mantenimiento;

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

/** El contrato HTTP de mantenimiento: estados y cuerpo de error {codigo, mensaje}. */
@WebMvcTest(ControladorOrdenes.class)
class ControladorOrdenesTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioOrdenes servicio;

    @Test
    void crearUnaOrdenResponde201Solicitada() throws Exception {
        when(servicio.crear(anyString(), anyString(), any(Long.class), any(), anyString()))
                .thenReturn(new Orden("Reparar", "Piscina", 800_000, null, LocalDateTime.of(2026, 11, 19, 10, 0)));

        mvc.perform(post("/ordenes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"descripcion\":\"Reparar\",\"zona\":\"Piscina\",\"costoEstimado\":800000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("SOLICITADA"));
    }

    @Test
    void aprobarSobreElTopeSinElConsejoResponde403() throws Exception {
        when(servicio.aprobar(anyString(), any(AprobadoPor.class), anyString()))
                .thenThrow(ErrorDeNegocio.sinPermiso("La orden la aprueba solo el Consejo."));

        mvc.perform(post("/ordenes/o1/aprobacion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"aprobadoPor\":\"ADMIN_CONJUNTO\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("E403"));
    }

    @Test
    void ejecutarSinMaterialesResponde400() throws Exception {
        mvc.perform(post("/ordenes/o1/ejecucion").contentType(MediaType.APPLICATION_JSON)
                .content("{\"materiales\":[]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }
}
