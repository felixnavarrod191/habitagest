package com.habitagest.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.areas.ControladorConsejo;
import com.habitagest.web.areas.ControladorContabilidad;
import com.habitagest.web.areas.ControladorPorteriaWeb;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Correcciones y mejoras que salieron de las pruebas web del Sprint 4: lista de presupuestos, resumen del Consejo que
 * conserva el mes y avisa cuando no hay datos, horas de entrada y salida de las visitas y página de error 404 propia.
 */
@WebMvcTest(controllers = {ControladorConsejo.class, ControladorContabilidad.class, ControladorPorteriaWeb.class})
@Import(SeguridadConfig.class)
class CorreccionesDelSprint4Test {

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

    @Test
    void cuotasYMoraMuestraLosPresupuestosGuardados() throws Exception {
        when(api.get("/finanzas/presupuestos/todos")).thenReturn(
                json("[{\"mes\":\"2026-11\",\"valor\":12000000},{\"mes\":\"2026-10\",\"valor\":9000000}]"));

        mvc.perform(get("/contabilidad/cuotas").with(user("conta").roles("CONTADOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Presupuestos guardados")))
                .andExpect(content().string(containsString("2026-11")))
                .andExpect(content().string(containsString("$12.000.000")))
                .andExpect(content().string(containsString("$9.000.000")));
    }

    @Test
    void sinPresupuestosLaTablaLoDice() throws Exception {
        mvc.perform(get("/contabilidad/cuotas").with(user("conta").roles("CONTADOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Todavía no hay presupuestos guardados.")));
    }

    @Test
    void elSelectorDelResumenConservaElMesConsultado() throws Exception {
        mvc.perform(get("/consejo/resumen").param("mes", "2026-02").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Resumen de 2026-02")))
                .andExpect(content().string(containsString("value=\"2026-02\"")));
    }

    @Test
    void siElMesNoEsValidoElSelectorProponeElMesActual() throws Exception {
        mvc.perform(get("/consejo/resumen").param("mes", "febrero").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("value=\"febrero\""))));
    }

    @Test
    void unMesSinCarteraNiGastosMuestraElAvisoYNoLosConceptosEnCero() throws Exception {
        when(api.get("/finanzas/resumen?mes=2026-02")).thenReturn(
                json("{\"mes\":\"2026-02\",\"totalCartera\":0,\"gastosMantenimiento\":0}"));

        mvc.perform(get("/consejo/resumen").param("mes", "2026-02").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No hay datos para este mes.")))
                .andExpect(content().string(not(containsString("Cartera pendiente de las cuotas del mes"))))
                .andExpect(content().string(not(containsString("Gastos de mantenimiento completados en el mes"))));
    }

    @Test
    void unMesConCarteraSigueMostrandoSusConceptos() throws Exception {
        when(api.get("/finanzas/resumen?mes=2026-11")).thenReturn(
                json("{\"mes\":\"2026-11\",\"totalCartera\":150000,\"gastosMantenimiento\":0}"));

        mvc.perform(get("/consejo/resumen").param("mes", "2026-11").with(user("consejo").roles("CONSEJO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Cartera pendiente de las cuotas del mes")))
                .andExpect(content().string(containsString("$150.000")))
                .andExpect(content().string(containsString("Gastos de mantenimiento completados en el mes")));
    }

    @Test
    void laTablaDeVisitasDelPorteroMuestraLaHoraDeEntradaYDeSalida() throws Exception {
        when(api.get("/porteria/visitas?fecha=2026-11-26")).thenReturn(json("[{\"id\":\"v1\",\"nombre\":\"Laura Gómez\","
                + "\"documento\":\"52000123\",\"apartamentoId\":\"ap1\",\"estado\":\"SALIO\"},"
                + "{\"id\":\"v2\",\"nombre\":\"Pedro Ríos\",\"documento\":\"80123456\",\"apartamentoId\":\"ap1\","
                + "\"estado\":\"AUTORIZADA\"}]"));
        when(api.get("/porteria/ingresos?fecha=2026-11-26")).thenReturn(json(
                "[{\"visitaId\":\"v1\",\"tipo\":\"ENTRADA\",\"hora\":\"2026-11-26T10:05:33\"},"
                        + "{\"visitaId\":\"v1\",\"tipo\":\"SALIDA\",\"hora\":\"2026-11-26T11:40:10\"}]"));

        mvc.perform(get("/porteria/visitas").param("fecha", "2026-11-26").with(user("portero1").roles("PORTERO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<th>Entrada</th>")))
                .andExpect(content().string(containsString("<th>Salida</th>")))
                .andExpect(content().string(containsString("10:05")))
                .andExpect(content().string(containsString("11:40")))
                .andExpect(content().string(containsString("Pedro Ríos")));
    }

    @Test
    void laPaginaDeErrorNoEncontradoTieneElDisenoDeLaWeb() throws Exception {
        try (InputStream archivo = new ClassPathResource("templates/error/404.html").getInputStream()) {
            String texto = new String(archivo.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(texto.contains("Página no encontrada"));
            assertTrue(texto.contains("Volver al inicio"));
        }
    }
}
