package com.habitagest.finanzas;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** REQ-PH-044 y REQ-PH-017: el revisor fiscal deja observaciones, pero sigue sin poder modificar pagos. */
@WebMvcTest({ControladorObservaciones.class, ControladorPagos.class, ControladorResumen.class})
class ControladorObservacionesTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioObservaciones servicio;

    @MockBean
    private ServicioPagos pagos;

    @MockBean
    private ServicioResumen resumen;

    @Test
    void elRevisorFiscalDejaUnaObservacionYResponde201ConSuAutor() throws Exception {
        when(servicio.dejar("Pago p-15", "Sin soporte.", "revisor1", "REVISOR_FISCAL"))
                .thenReturn(new Observacion("Pago p-15", "Sin soporte.", "revisor1", LocalDateTime.of(2026, 11, 26, 10, 0)));

        mvc.perform(post("/observaciones").header("X-Rol", "REVISOR_FISCAL").header("X-Usuario", "revisor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"referencia\":\"Pago p-15\",\"texto\":\"Sin soporte.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autor").value("revisor1"))
                .andExpect(jsonPath("$.referencia").value("Pago p-15"));

        verify(servicio).dejar("Pago p-15", "Sin soporte.", "revisor1", "REVISOR_FISCAL");
    }

    @Test
    void elRevisorFiscalSigueSinPoderRegistrarUnPago() throws Exception {
        mvc.perform(post("/pagos").header("X-Rol", "REVISOR_FISCAL").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cuotaId\":\"c1\",\"valor\":1000}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("E403"));
    }

    @Test
    void sinReferenciaOSinTextoLaObservacionSeRechazaConE400() throws Exception {
        mvc.perform(post("/observaciones").header("X-Rol", "REVISOR_FISCAL").header("X-Usuario", "revisor1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"referencia\":\"\",\"texto\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("E400"));
        mvc.perform(post("/observaciones").header("X-Rol", "REVISOR_FISCAL").header("X-Usuario", "revisor1")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"referencia\":\"Pago p-1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void elAdministradorConsultaLasObservacionesConFechaYAutor() throws Exception {
        when(servicio.listar("ADMIN_CONJUNTO")).thenReturn(List.of(
                new Observacion("Orden o-2", "Falta la factura.", "revisor1", LocalDateTime.of(2026, 11, 26, 10, 0))));

        mvc.perform(get("/observaciones").header("X-Rol", "ADMIN_CONJUNTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].autor").value("revisor1"))
                .andExpect(jsonPath("$[0].fecha").exists());
    }

    @Test
    void elResumenSeConsultaConElMesYSinElMesDaE400() throws Exception {
        when(resumen.resumen("2026-11")).thenReturn(new ResumenFinanciero("2026-11", 200000, 80000));

        mvc.perform(get("/resumen").param("mes", "2026-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCartera").value(200000.0))
                .andExpect(jsonPath("$.gastosMantenimiento").value(80000.0));
        mvc.perform(get("/resumen")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("E400"));
        verify(resumen).resumen(anyString());
    }
}
