package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.ControladorUsuarios;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

/** AC-002.1 (REQ-PH-002): el usuario queda con su rol y la contraseña no se guarda en texto plano. */
@WebMvcTest(ControladorUsuarios.class)
@Import(SeguridadConfig.class)
class UsuariosControladorTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder codificador;

    @MockBean
    private UsuarioRepository repositorio;

    @Test
    void creaElUsuarioConSuRolYLaContrasenaCifrada() throws Exception {
        when(repositorio.existsByUsuario("contador1")).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "contador1").param("contrasena", "ClaveDePrueba1").param("rol", "CONTADOR"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio).save(guardado.capture());
        assertEquals(Rol.CONTADOR, guardado.getValue().getRol());
        assertNotEquals("ClaveDePrueba1", guardado.getValue().getContrasenaCifrada());
        assertTrue(codificador.matches("ClaveDePrueba1", guardado.getValue().getContrasenaCifrada()));
    }

    @Test
    void rechazaUnUsuarioConElMismoNombre() throws Exception {
        when(repositorio.existsByUsuario("contador1")).thenReturn(true);

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "contador1").param("contrasena", "ClaveDePrueba1").param("rol", "CONTADOR"))
                .andExpect(status().is3xxRedirection());

        verify(repositorio, never()).save(any(Usuario.class));
    }

    @Test
    void soloElAdministradorDelSistemaCreaUsuarios() throws Exception {
        mvc.perform(post("/sistema/usuarios").with(user("portero1").roles("PORTERO")).with(csrf())
                .param("usuario", "otro").param("contrasena", "ClaveDePrueba1").param("rol", "CONTADOR"))
                .andExpect(status().isForbidden());

        verify(repositorio, never()).save(any(Usuario.class));
    }
}
