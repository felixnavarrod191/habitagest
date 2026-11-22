package com.habitagest.porteria;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** El contrato HTTP de portería: estados y cuerpo de error {codigo, mensaje}. */
@WebMvcTest(ControladorPorteria.class)
class ControladorPorteriaTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioPorteria servicio;

    @Test
    void unVisitanteNoAutorizadoResponde404() throws Exception {
        when(servicio.registrar(anyString(), any(TipoIngreso.class), anyString()))
                .thenThrow(ErrorDeNegocio.noEncontrado("El visitante no fue autorizado por ningún residente (RN-10)."));

        mvc.perform(post("/ingresos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"visitaId\":\"x\",\"tipo\":\"ENTRADA\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("E404"));
    }

    @Test
    void unTipoDeIngresoInvalidoResponde400() throws Exception {
        mvc.perform(post("/ingresos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"visitaId\":\"x\",\"tipo\":\"VOLANDO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }

    @Test
    void autorizarUnaVisitaSinDocumentoResponde400() throws Exception {
        mvc.perform(post("/visitas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"apartamentoId\":\"a1\",\"nombre\":\"Ana\",\"fecha\":\"2026-11-20\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }
}
