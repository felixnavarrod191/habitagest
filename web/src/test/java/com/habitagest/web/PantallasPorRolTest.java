package com.habitagest.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.areas.ControladorAdministracion;
import com.habitagest.web.areas.ControladorConsejo;
import com.habitagest.web.areas.ControladorContabilidad;
import com.habitagest.web.areas.ControladorMantenimientoWeb;
import com.habitagest.web.areas.ControladorPorteriaWeb;
import com.habitagest.web.areas.ControladorResidentes;
import com.habitagest.web.areas.ControladorRevision;
import com.habitagest.web.areas.ControladorSistema;
import com.habitagest.web.areas.ControladorVigilancia;
import com.habitagest.web.pantallas.Areas;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.ControladorUsuarios;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** Cada rol abre todas las pantallas de su módulo (aunque los servicios no respondan) y ninguna ajena. */
@WebMvcTest(controllers = {ControladorInicio.class, ControladorAreas.class, ControladorUsuarios.class,
        ControladorSistema.class, ControladorAdministracion.class, ControladorConsejo.class,
        ControladorResidentes.class, ControladorPorteriaWeb.class, ControladorVigilancia.class,
        ControladorContabilidad.class, ControladorRevision.class, ControladorMantenimientoWeb.class})
@Import(SeguridadConfig.class)
class PantallasPorRolTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    @MockBean
    private ClienteApi api;

    static Stream<Arguments> pantallas() {
        List<Arguments> casos = new ArrayList<>();
        for (Rol rol : Rol.values()) {
            Areas.menu(rol).forEach(enlace -> casos.add(Arguments.of(rol, enlace.ruta())));
        }
        return casos.stream();
    }

    @ParameterizedTest(name = "{0} abre {1}")
    @MethodSource("pantallas")
    void cadaRolAbreLasPantallasDeSuModulo(Rol rol, String ruta) throws Exception {
        mvc.perform(get(ruta).with(user("prueba").roles(rol.name()))).andExpect(status().isOk());
    }

    static Stream<Arguments> ajenas() {
        Rol[] roles = Rol.values();
        List<Arguments> casos = new ArrayList<>();
        for (int i = 0; i < roles.length; i++) {
            Rol otro = roles[(i + 1) % roles.length];
            casos.add(Arguments.of(roles[i], Areas.menu(otro).get(0).ruta()));
        }
        return casos.stream();
    }

    @ParameterizedTest(name = "{0} no puede abrir {1}")
    @MethodSource("ajenas")
    void ningunRolAbreUnaPantallaAjena(Rol rol, String ruta) throws Exception {
        mvc.perform(get(ruta).with(user("prueba").roles(rol.name()))).andExpect(status().isForbidden());
    }

    @Test
    void sinSesionLasPantallasPidenIniciarSesion() throws Exception {
        mvc.perform(get("/administracion/finanzas")).andExpect(status().is3xxRedirection());
    }
}
