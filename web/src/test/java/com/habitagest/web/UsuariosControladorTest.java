package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.seguridad.SeguridadConfig;
import com.habitagest.web.usuarios.ControladorUsuarios;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

/** AC-002.1 (REQ-PH-002): el usuario queda con su rol y la contraseña no se guarda en texto plano; propietarios y residentes, con su apartamento. */
@WebMvcTest(ControladorUsuarios.class)
@Import(SeguridadConfig.class)
class UsuariosControladorTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PasswordEncoder codificador;

    @MockBean
    private UsuarioRepository repositorio;

    @MockBean
    private ClienteApi api;

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

    private void hayApartamentos() throws Exception {
        when(api.get("/copropiedad/apartamentos")).thenReturn(new ObjectMapper()
                .readTree("[{\"id\":\"ap7\",\"torre\":\"1\",\"numero\":\"301\"}]"));
    }

    @Test
    void creaUnPropietarioConSuApartamentoYLoRegistraEnCopropiedad() throws Exception {
        hayApartamentos();
        when(repositorio.existsByUsuario("carlos")).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "carlos").param("contrasena", "ClaveDePrueba1").param("rol", "PROPIETARIO")
                .param("nombreCompleto", "Carlos Pérez").param("documento", "1001").param("apartamentoId", "ap7"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<Map<String, Object>> cuerpo = ArgumentCaptor.forClass(Map.class);
        verify(api).post(eq("/copropiedad/propietarios"), cuerpo.capture());
        assertEquals("ap7", cuerpo.getValue().get("apartamentoId"));
        assertEquals("Carlos Pérez", cuerpo.getValue().get("nombre"));
        assertEquals("1001", cuerpo.getValue().get("documento"));
        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio).save(guardado.capture());
        assertEquals("ap7", guardado.getValue().getApartamentoId());
        assertEquals("Torre 1 · Apto 301", guardado.getValue().getApartamentoNombre());
        assertEquals("Carlos Pérez", guardado.getValue().getNombreCompleto());
        assertEquals("1001", guardado.getValue().getDocumento());
    }

    @Test
    void creaUnResidenteYLoRegistraComoResidente() throws Exception {
        hayApartamentos();
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "andres").param("contrasena", "ClaveDePrueba1").param("rol", "RESIDENTE")
                .param("nombreCompleto", "Andrés Rojas").param("documento", "2002").param("apartamentoId", "ap7"))
                .andExpect(status().is3xxRedirection());

        verify(api).post(eq("/copropiedad/residentes"), anyMap());
        verify(repositorio).save(any(Usuario.class));
    }

    @Test
    void unPropietarioOUnResidenteNecesitanApartamentoNombreYDocumento() throws Exception {
        hayApartamentos();

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "sinapto").param("contrasena", "ClaveDePrueba1").param("rol", "PROPIETARIO")
                .param("nombreCompleto", "Alguien").param("documento", "1"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "sindoc").param("contrasena", "ClaveDePrueba1").param("rol", "RESIDENTE")
                .param("apartamentoId", "ap7"))
                .andExpect(status().is3xxRedirection());

        verify(repositorio, never()).save(any(Usuario.class));
        verify(api, never()).post(any(String.class), anyMap());
    }

    @Test
    void noGuardaElUsuarioSiCopropiedadRechazaAlSegundoPropietario() throws Exception {
        hayApartamentos();
        when(api.post(eq("/copropiedad/propietarios"), anyMap()))
                .thenThrow(new ErrorDelServicio(409, "El apartamento ya tiene un propietario responsable."));

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "otro").param("contrasena", "ClaveDePrueba1").param("rol", "PROPIETARIO")
                .param("nombreCompleto", "Otro Dueño").param("documento", "3003").param("apartamentoId", "ap7"))
                .andExpect(status().is3xxRedirection());

        verify(repositorio, never()).save(any(Usuario.class));
    }

    @Test
    void losDemasRolesNoLlevanApartamento() throws Exception {
        when(repositorio.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        mvc.perform(post("/sistema/usuarios").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "portero1").param("contrasena", "ClaveDePrueba1").param("rol", "PORTERO")
                .param("apartamentoId", "ap7").param("nombreCompleto", "X").param("documento", "9"))
                .andExpect(status().is3xxRedirection());

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio).save(guardado.capture());
        assertNull(guardado.getValue().getApartamentoId());
        assertNull(guardado.getValue().getNombreCompleto());
        verify(api, never()).post(any(String.class), anyMap());
    }

    @Test
    void asignaElApartamentoAUnResidenteQueNoLoTenia() throws Exception {
        hayApartamentos();
        Usuario rosa = new Usuario("rosa", "x", Rol.RESIDENTE, true);
        when(repositorio.findByUsuario("rosa")).thenReturn(Optional.of(rosa));

        mvc.perform(post("/sistema/usuarios/apartamento").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "rosa").param("apartamentoId", "ap7")
                .param("nombreCompleto", "Rosa Díaz").param("documento", "4004"))
                .andExpect(status().is3xxRedirection());

        verify(api).post(eq("/copropiedad/residentes"), anyMap());
        verify(repositorio).save(rosa);
        assertEquals("ap7", rosa.getApartamentoId());
        assertEquals("Rosa Díaz", rosa.getNombreCompleto());
    }

    @Test
    void noCambiaElApartamentoDeQuienYaLoTiene() throws Exception {
        Usuario rosa = new Usuario("rosa", "x", Rol.RESIDENTE, true);
        rosa.setApartamentoId("ap1");
        when(repositorio.findByUsuario("rosa")).thenReturn(Optional.of(rosa));

        mvc.perform(post("/sistema/usuarios/apartamento").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "rosa").param("apartamentoId", "ap7")
                .param("nombreCompleto", "Rosa Díaz").param("documento", "4004"))
                .andExpect(status().is3xxRedirection());

        verify(repositorio, never()).save(any(Usuario.class));
        assertEquals("ap1", rosa.getApartamentoId());
    }

    @Test
    void soloPropietariosYResidentesTienenApartamento() throws Exception {
        Usuario contador = new Usuario("conta", "x", Rol.CONTADOR, true);
        when(repositorio.findByUsuario("conta")).thenReturn(Optional.of(contador));

        mvc.perform(post("/sistema/usuarios/apartamento").with(user("admin").roles("ADMIN_SISTEMA")).with(csrf())
                .param("usuario", "conta").param("apartamentoId", "ap7")
                .param("nombreCompleto", "Conta").param("documento", "5005"))
                .andExpect(status().is3xxRedirection());

        verify(repositorio, never()).save(any(Usuario.class));
    }
}
