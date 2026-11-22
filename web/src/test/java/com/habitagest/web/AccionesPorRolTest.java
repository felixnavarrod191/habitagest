package com.habitagest.web;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.areas.ControladorAdministracion;
import com.habitagest.web.areas.ControladorConsejo;
import com.habitagest.web.areas.ControladorContabilidad;
import com.habitagest.web.areas.ControladorMantenimientoWeb;
import com.habitagest.web.areas.ControladorPorteriaWeb;
import com.habitagest.web.areas.ControladorResidentes;
import com.habitagest.web.areas.ControladorRevision;
import com.habitagest.web.areas.ControladorSistema;
import com.habitagest.web.areas.ControladorVigilancia;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

/** Lo que hace cada rol desde su pantalla llega a los servicios con los datos correctos y el rol correcto. */
@WebMvcTest(controllers = {ControladorSistema.class, ControladorAdministracion.class, ControladorConsejo.class,
        ControladorResidentes.class, ControladorPorteriaWeb.class, ControladorVigilancia.class,
        ControladorContabilidad.class, ControladorRevision.class, ControladorMantenimientoWeb.class})
@Import(SeguridadConfig.class)
class AccionesPorRolTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    @MockBean
    private ClienteApi api;

    private static Map<String, Object> cuerpo(Object... pares) {
        Map<String, Object> mapa = new java.util.LinkedHashMap<>();
        for (int i = 0; i + 1 < pares.length; i += 2) {
            mapa.put((String) pares[i], pares[i + 1]);
        }
        return mapa;
    }

    private static Map<String, Object> igual(Map<String, Object> esperado) {
        return ArgumentMatchers.<Map<String, Object>>eq(esperado);
    }

    private static JsonNode json(String texto) throws Exception {
        return JSON.readTree(texto);
    }

    @Test
    void elAdministradorCreaUnaTorreYVuelveALaPantalla() throws Exception {
        mvc.perform(post("/administracion/copropiedad/torres").param("nombre", " Torre 9 ").with(csrf())
                        .with(user("admin").roles("ADMIN_CONJUNTO")))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/administracion/copropiedad"))
                .andExpect(flash().attribute("mensaje", "Torre creada."));
        verify(api).post(eq("/copropiedad/torres"), igual(cuerpo("nombre", "Torre 9")));
    }

    @Test
    void siUnServicioRechazaLaAccionLaPantallaMuestraSuMensaje() throws Exception {
        when(api.post(eq("/copropiedad/torres"), anyMap())).thenThrow(new ErrorDelServicio(409, "La torre ya existe."));
        mvc.perform(post("/administracion/copropiedad/torres").param("nombre", "A").with(csrf())
                        .with(user("admin").roles("ADMIN_CONJUNTO")))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "La torre ya existe."));
    }

    @Test
    void sinElTokenCsrfLaAccionSeRechaza() throws Exception {
        mvc.perform(post("/administracion/copropiedad/torres").param("nombre", "A")
                        .with(user("admin").roles("ADMIN_CONJUNTO")))
                .andExpect(status().isForbidden());
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }

    @Test
    void elAdministradorApruebaConSuRolYElConsejoConElSuyo() throws Exception {
        mvc.perform(post("/administracion/mantenimiento/aprobar").param("id", "o1").with(csrf())
                .with(user("admin").roles("ADMIN_CONJUNTO"))).andExpect(status().is3xxRedirection());
        verify(api).post(eq("/mantenimiento/ordenes/o1/aprobacion"), igual(cuerpo("aprobadoPor", "ADMIN_CONJUNTO")));
        mvc.perform(post("/consejo/ordenes/aprobar").param("id", "o2").with(csrf())
                .with(user("consejo").roles("CONSEJO"))).andExpect(status().is3xxRedirection());
        verify(api).post(eq("/mantenimiento/ordenes/o2/aprobacion"), igual(cuerpo("aprobadoPor", "CONSEJO")));
    }

    private Usuario conApartamento(String nombre, Rol rol, String apartamentoId) {
        Usuario persona = new Usuario(nombre, "x", rol, true);
        persona.setApartamentoId(apartamentoId);
        return persona;
    }

    @Test
    void elResidenteReservaConElApartamentoQueTieneAsignadoAunqueEnvieOtro() throws Exception {
        when(usuarios.findByUsuario("rosa")).thenReturn(Optional.of(conApartamento("rosa", Rol.RESIDENTE, "ap1")));
        when(api.get("/copropiedad/apartamentos/ap1")).thenReturn(json("{\"id\":\"ap1\",\"torre\":\"1\",\"numero\":\"301\"}"));
        mvc.perform(post("/residente/reservas").param("zonaId", "z1").param("fecha", "2026-11-25")
                        .param("horaInicio", "10:00").param("horaFin", "12:00").param("apartamentoId", "ap2")
                        .with(csrf()).with(user("rosa").roles("RESIDENTE")))
                .andExpect(flash().attribute("mensaje", "Reserva confirmada."));
        verify(api).post(eq("/reservas/reservas"), igual(cuerpo("zonaId", "z1", "apartamentoId", "ap1",
                "inicio", "2026-11-25T10:00", "fin", "2026-11-25T12:00")));
    }

    @Test
    void sinApartamentoAsignadoElResidenteSoloVeElAviso() throws Exception {
        mvc.perform(get("/residente/reservas").with(user("rosa").roles("RESIDENTE")))
                .andExpect(status().isOk()).andExpect(model().attributeExists("p"));
        verify(api, never()).get(ArgumentMatchers.startsWith("/reservas/reservas"));
    }

    @Test
    void sinApartamentoAsignadoNoSePuedeReservar() throws Exception {
        mvc.perform(post("/residente/reservas").param("zonaId", "z1").param("fecha", "2026-11-25")
                        .param("horaInicio", "10:00").param("horaFin", "12:00").with(csrf())
                        .with(user("rosa").roles("RESIDENTE")))
                .andExpect(flash().attributeExists("error"));
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }

    @Test
    void elResidenteSoloVeLasReservasDeSuApartamento() throws Exception {
        when(usuarios.findByUsuario("rosa")).thenReturn(Optional.of(conApartamento("rosa", Rol.RESIDENTE, "ap1")));
        when(api.get(ArgumentMatchers.anyString())).thenReturn(json("[]"));
        mvc.perform(get("/residente/reservas").with(user("rosa").roles("RESIDENTE")))
                .andExpect(status().isOk());
        verify(api).get("/reservas/reservas?apartamentoId=ap1");
    }

    @Test
    void elMenuLateralMuestraElNombreDelUsuarioYSuApartamento() throws Exception {
        Usuario rosa = conApartamento("rosa", Rol.RESIDENTE, "ap1");
        rosa.setApartamentoNombre("Torre 1 · Apto 102");
        when(usuarios.findByUsuario("rosa")).thenReturn(Optional.of(rosa));
        when(api.get(ArgumentMatchers.anyString())).thenReturn(json("[]"));
        mvc.perform(get("/residente/reservas").with(user("rosa").roles("RESIDENTE")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("rosa")))
                .andExpect(content().string(containsString("Torre 1 · Apto 102")));
    }

    @Test
    void laPqrSeRadicaConElUsuarioDeLaSesion() throws Exception {
        mvc.perform(post("/propietario/pqr").param("asunto", "Ruido").param("descripcion", "Fuerte en la noche")
                        .with(csrf()).with(user("pedro").roles("PROPIETARIO")))
                .andExpect(redirectedUrl("/propietario/pqr"));
        verify(api).post(eq("/pqr/pqr"), igual(cuerpo("residenteId", "pedro", "asunto", "Ruido",
                "descripcion", "Fuerte en la noche")));
    }

    @Test
    void elPorteroRegistraLaEntradaYElVigilanteUnaNovedad() throws Exception {
        mvc.perform(post("/porteria/visitas/ingreso").param("visitaId", "v1").param("tipo", "ENTRADA")
                        .param("fecha", "2026-11-18").with(csrf()).with(user("portero").roles("PORTERO")))
                .andExpect(redirectedUrl("/porteria/visitas?fecha=2026-11-18"));
        verify(api).post(eq("/porteria/ingresos"), igual(cuerpo("visitaId", "v1", "tipo", "ENTRADA")));
        mvc.perform(post("/vigilancia/novedades").param("texto", "Puerta abierta").with(csrf())
                .with(user("vigilante").roles("VIGILANTE"))).andExpect(redirectedUrl("/vigilancia/novedades"));
        verify(api).post(eq("/porteria/novedades"), igual(cuerpo("texto", "Puerta abierta")));
    }

    @Test
    void elContadorRegistraUnPagoYElRevisorFiscalNoPuedeEscribir() throws Exception {
        mvc.perform(post("/contabilidad/pagos").param("cuotaId", "c1").param("valor", "150000,50").with(csrf())
                .with(user("contador").roles("CONTADOR"))).andExpect(redirectedUrl("/contabilidad/pagos"));
        verify(api).post(eq("/finanzas/pagos"), igual(cuerpo("cuotaId", "c1", "valor", 150000.5)));
        mvc.perform(post("/contabilidad/pagos").param("cuotaId", "c1").param("valor", "1").with(csrf())
                .with(user("revisor").roles("REVISOR_FISCAL"))).andExpect(status().isForbidden());
    }

    @Test
    void unValorNoNumericoSeAvisaSinLlamarAlServicio() throws Exception {
        mvc.perform(post("/contabilidad/pagos").param("cuotaId", "c1").param("valor", "abc").with(csrf())
                        .with(user("contador").roles("CONTADOR")))
                .andExpect(flash().attribute("error", "Escribe un número válido."));
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }

    @Test
    void elEncargadoCreaUnaOrdenSolicitada() throws Exception {
        mvc.perform(post("/mantenimiento/ordenes/crear").param("descripcion", "Cambiar bomba").param("zona", "Piscina")
                        .param("costoEstimado", "500000").with(csrf()).with(user("tecnico").roles("ENCARGADO_MANTENIMIENTO")))
                .andExpect(redirectedUrl("/mantenimiento/ordenes"))
                .andExpect(flash().attribute("mensaje", "Orden creada en estado Solicitada."));
        verify(api).post(eq("/mantenimiento/ordenes"), igual(cuerpo("descripcion", "Cambiar bomba", "zona", "Piscina",
                "costoEstimado", 500000L)));
    }

    @Test
    void elEncargadoCompletaUnaOrdenConSusMateriales() throws Exception {
        mvc.perform(post("/mantenimiento/ordenes/ejecucion").param("id", "o1").param("descripcion1", "Cable")
                        .param("valor1", "40000").param("descripcion2", "Cinta").param("valor2", "5000")
                        .with(csrf()).with(user("tecnico").roles("ENCARGADO_MANTENIMIENTO")))
                .andExpect(redirectedUrl("/mantenimiento/ordenes"));
        verify(api).post(eq("/mantenimiento/ordenes/o1/ejecucion"), igual(cuerpo("materiales", java.util.List.of(
                cuerpo("descripcion", "Cable", "valor", 40000L), cuerpo("descripcion", "Cinta", "valor", 5000L)))));
    }

    @Test
    void elProveedorSoloPideSusPropiasOrdenes() throws Exception {
        mvc.perform(get("/proveedor/ordenes").with(user("ferreteria").roles("PROVEEDOR"))).andExpect(status().isOk());
        verify(api).get("/mantenimiento/ordenes?proveedorId=ferreteria");
    }

    @Test
    void unRolNoPuedeEjecutarAccionesDeOtroRol() throws Exception {
        mvc.perform(post("/administracion/pqr/cierre").param("id", "p1").param("respuesta", "ok").with(csrf())
                .with(user("rosa").roles("RESIDENTE"))).andExpect(status().isForbidden());
        verify(api, never()).post(ArgumentMatchers.anyString(), anyMap());
    }
}
