package com.habitagest.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** AC-001.1: un portero con la sesión iniciada ve solo las opciones de portería. */
@WebMvcTest(controllers = {ControladorInicio.class, ControladorAreas.class})
@Import(SeguridadConfig.class)
class InicioPorRolTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    @Test
    void elPorteroVeSoloLasOpcionesDePorteria() throws Exception {
        mvc.perform(get("/").with(user("portero1").roles("PORTERO")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/porteria")))
                .andExpect(content().string(not(containsString("/contabilidad"))))
                .andExpect(content().string(not(containsString("/sistema"))));
    }

    @Test
    void elContadorVeSoloLasOpcionesDeContabilidad() throws Exception {
        mvc.perform(get("/").with(user("contador1").roles("CONTADOR")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/contabilidad")))
                .andExpect(content().string(not(containsString("/porteria"))));
    }
}
