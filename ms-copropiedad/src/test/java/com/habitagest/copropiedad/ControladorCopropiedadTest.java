package com.habitagest.copropiedad;

import static org.mockito.ArgumentMatchers.anyDouble;
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

/** El contrato HTTP de copropiedad: estados y cuerpo de error {codigo, mensaje}. */
@WebMvcTest(ControladorCopropiedad.class)
class ControladorCopropiedadTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioCopropiedad servicio;

    @Test
    void registrarUnApartamentoResponde201() throws Exception {
        when(servicio.crearApartamento(anyString(), anyString(), anyDouble()))
                .thenReturn(new Apartamento("1", "301", 1.5));

        mvc.perform(post("/apartamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"torre\":\"1\",\"numero\":\"301\",\"coeficiente\":1.5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("301"));
    }

    @Test
    void unaReglaDeNegocioResponde409ConElCuerpoDelContrato() throws Exception {
        when(servicio.crearApartamento(anyString(), anyString(), anyDouble()))
                .thenThrow(ErrorDeNegocio.reglaDeNegocio("La suma de los coeficientes sería 101 %, que supera el 100 %."));

        mvc.perform(post("/apartamentos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"torre\":\"1\",\"numero\":\"302\",\"coeficiente\":2}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("E409"));
    }

    @Test
    void unaSolicitudSinNombreResponde400() throws Exception {
        mvc.perform(post("/torres").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
    }
}
