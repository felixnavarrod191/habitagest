package com.habitagest.web;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.areas.ControladorAdministracion;
import com.habitagest.web.areas.ControladorConsejo;
import com.habitagest.web.areas.ControladorRevision;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * PBI-12: el Consejo ve el total de cartera y de gastos del mes (REQ-PH-043, AC-043.1) y el revisor fiscal deja
 * observaciones que el administrador ve con fecha y autor (REQ-PH-044, AC-044.1).
 */
@WebMvcTest(controllers = {ControladorConsejo.class, ControladorRevision.class, ControladorAdministracion.class})
@Import(SeguridadConfig.class)
class ResumenYObservacionesTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    @MockBean
    private ClienteApi api;

    private static JsonNode json(String texto) throws Exception {
        return JSON.readTree(texto);
    }

    private static Map<String, Object> cuerpo(String referencia, String texto) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("referencia", referencia);
        mapa.put("texto", texto);
        return mapa;
    }

    @Test
    void elConsejoVeElTotalDeCarteraYDeGastosDelMes() throws Exception {
        when(api.get("/finanzas/resumen?mes=2026-11")).thenReturn(
                json("{\"mes\":\"2026-11\",\"totalCartera\":200000,\"gastosMantenimiento\":80000}"));

        mvc.perform(get("/consejo/resumen").param("mes", "2026-11").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cartera pendiente")))
                .andExpect(content().string(containsString("$200.000")))
                .andExpect(content().string(containsString("Gastos de mantenimiento")))
                .andExpect(content().string(containsString("$80.000")));
        verify(api).get("/finanzas/resumen?mes=2026-11");
    }

    @Test
    void sinDatosDelMesElResumenSeMuestraVacioSinFallar() throws Exception {
        mvc.perform(get("/consejo/resumen").param("mes", "2026-09").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay datos para este mes.")));
    }

    @Test
    void elRevisorFiscalDejaUnaObservacionConLaReferenciaYElTextoLimpios() throws Exception {
        mvc.perform(post("/revision/observaciones").param("tipo", "Pago").param("identificador", " p-15 ")
                        .param("texto", " Sin soporte. ").with(csrf()).with(user("revisor1").roles("REVISOR_FISCAL")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/revision/observaciones"))
                .andExpect(flash().attribute("mensaje", "Observación guardada."));
        verify(api).post(eq("/finanzas/observaciones"), eq(cuerpo("Pago p-15", "Sin soporte.")));
    }

    @Test
    void siFinanzasRechazaLaObservacionLaPantallaMuestraSuMensaje() throws Exception {
        when(api.post(eq("/finanzas/observaciones"), anyMap()))
                .thenThrow(new ErrorDelServicio(403, "Solo el revisor fiscal puede dejar observaciones de auditoría."));

        mvc.perform(post("/revision/observaciones").param("tipo", "Orden de mantenimiento")
                        .param("identificador", "o-2").param("texto", "Falta la factura.").with(csrf())
                        .with(user("revisor1").roles("REVISOR_FISCAL")))
                .andExpect(flash().attribute("error", "Solo el revisor fiscal puede dejar observaciones de auditoría."));
    }

    @Test
    void sinElTokenCsrfLaObservacionSeRechaza() throws Exception {
        mvc.perform(post("/revision/observaciones").param("tipo", "Pago").param("identificador", "p-1")
                        .param("texto", "x").with(user("revisor1").roles("REVISOR_FISCAL")))
                .andExpect(status().isForbidden());
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }

    @Test
    void elRevisorYElAdministradorVenLasObservacionesConFechaYAutor() throws Exception {
        when(api.get("/finanzas/observaciones")).thenReturn(json("[{\"id\":\"1\",\"referencia\":\"Pago p-15\","
                + "\"texto\":\"Sin soporte.\",\"autor\":\"revisor1\",\"fecha\":\"2026-11-26T10:00:00\"}]"));

        for (String[] caso : new String[][] {{"/revision/observaciones", "REVISOR_FISCAL"},
                {"/administracion/observaciones", "ADMIN_CONJUNTO"}}) {
            mvc.perform(get(caso[0]).with(user("prueba").roles(caso[1])))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("revisor1")))
                    .andExpect(content().string(containsString("Pago p-15")))
                    .andExpect(content().string(containsString("Sin soporte.")))
                    .andExpect(content().string(containsString("2026-11-26 10:00")));
        }
    }

    @Test
    void losDemasRolesNoAbrenNiEscribenObservaciones() throws Exception {
        mvc.perform(get("/revision/observaciones").with(user("c").roles("CONTADOR"))).andExpect(status().isForbidden());
        mvc.perform(get("/administracion/observaciones").with(user("r").roles("REVISOR_FISCAL")))
                .andExpect(status().isForbidden());
        mvc.perform(post("/revision/observaciones").param("tipo", "Pago").param("identificador", "p-1")
                        .param("texto", "x").with(csrf()).with(user("a").roles("ADMIN_CONJUNTO")))
                .andExpect(status().isForbidden());
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }
}
