package com.habitagest.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/** Página de aterrizaje: sin sesión se ve el portal del conjunto con el acceso; con sesión, el área del rol. */
@WebMvcTest(controllers = {ControladorInicio.class, ControladorAreas.class})
@Import(SeguridadConfig.class)
class AterrizajeTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsuarioRepository usuarios;

    @Test
    void sinSesionSeVeElPortalDelConjuntoConElFormularioDeAcceso() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Portal web del Conjunto Residencial Torres del Mirador")))
                .andExpect(content().string(containsString("name=\"username\"")))
                .andExpect(content().string(containsString("name=\"password\"")))
                .andExpect(content().string(containsString("felixnavarro111o@gmail.com")))
                .andExpect(content().string(containsString("pandalcoholic@gmail.com")))
                .andExpect(content().string(containsString("estivenalexanderr7@gmail.com")));
    }

    @Test
    void conSesionSeVeElAreaDelRolYNoLaPaginaDeAterrizaje() throws Exception {
        mvc.perform(get("/").with(user("portero1").roles("PORTERO")))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("felixnavarro111o@gmail.com"))));
    }

    @Test
    void elAccesoAntiguoLlevaALaPaginaDeAterrizaje() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
