package com.habitagest.finanzas;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** AC-017.1 (REQ-PH-017): un revisor fiscal no puede modificar un pago, pero sí consultar. */
@WebMvcTest(ControladorPagos.class)
class ControladorPagosTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ServicioPagos servicio;

    @Test
    void elRevisorFiscalNoPuedeRegistrarUnPago() throws Exception {
        mvc.perform(post("/pagos").header("X-Rol", "REVISOR_FISCAL").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cuotaId\":\"c1\",\"valor\":1000}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("E403"));
    }

    @Test
    void elRevisorFiscalSiPuedeConsultarLaCartera() throws Exception {
        when(servicio.cartera(null)).thenReturn(List.of());

        mvc.perform(get("/cartera").header("X-Rol", "REVISOR_FISCAL")).andExpect(status().isOk());
    }

    @Test
    void elContadorRegistraElPagoYResponde201() throws Exception {
        when(servicio.registrarPago(anyString(), org.mockito.ArgumentMatchers.anyDouble(), anyString()))
                .thenReturn(new CuotaVista("c1", "a1", "2026-11", 150000, 50000, 0, "PENDIENTE"));

        mvc.perform(post("/pagos").header("X-Rol", "CONTADOR").contentType(MediaType.APPLICATION_JSON)
                .content("{\"cuotaId\":\"c1\",\"valor\":100000}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saldo").value(50000.0));
    }
}
