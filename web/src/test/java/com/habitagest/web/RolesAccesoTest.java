package com.habitagest.web;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

/**
 * NFR-PH-002, REQ-PH-001 y REQ-PH-003: cada uno de los 11 roles entra a su área y es rechazado en las demás.
 * AC-003.1: un residente que intenta abrir una pantalla del contador es rechazado.
 */
@WebMvcTest(controllers = {ControladorInicio.class, ControladorAreas.class, ControladorUsuarios.class})
@Import(SeguridadConfig.class)
class RolesAccesoTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    static Stream<Arguments> combinaciones() {
        List<Arguments> casos = new ArrayList<>();
        for (Rol rol : Rol.values()) {
            for (Rol area : Rol.values()) {
                casos.add(Arguments.of(rol, area, rol == area));
            }
        }
        return casos.stream();
    }

    @ParameterizedTest(name = "{0} en el área de {1}: permitido = {2}")
    @MethodSource("combinaciones")
    void cadaRolEntraSoloASuArea(Rol rol, Rol area, boolean permitido) throws Exception {
        var peticion = get(area.rutaBase()).with(user("prueba").roles(rol.name()));
        mvc.perform(peticion).andExpect(permitido ? status().isOk() : status().isForbidden());
    }

    @Test
    void unResidenteNoPuedeAbrirUnaPantallaDelContador() throws Exception {
        mvc.perform(get("/contabilidad").with(user("prueba").roles("RESIDENTE"))).andExpect(status().isForbidden());
    }

    @Test
    void sinSesionRedirigeAlInicioDeSesion() throws Exception {
        mvc.perform(get("/sistema")).andExpect(status().is3xxRedirection());
    }
}
