package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Todo usuario lleva nombre completo y documento, también el administrador y los usuarios de demostración. */
class DatosInicialesTest {

    private final UsuarioRepository repositorio = mock(UsuarioRepository.class);
    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    @Test
    void elAdministradorYLosUsuariosDeDemostracionSeCreanConNombreYDocumento() {
        when(repositorio.count()).thenReturn(0L);

        new DatosIniciales(repositorio, codificador, "ClaveAdmin2026", true, "ClaveDemo2026").run();

        ArgumentCaptor<Usuario> guardados = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorio, times(1 + Rol.values().length)).save(guardados.capture());
        for (Usuario usuario : guardados.getAllValues()) {
            assertNotNull(usuario.getNombreCompleto(), usuario.getUsuario());
            assertFalse(usuario.getNombreCompleto().isBlank(), usuario.getUsuario());
            assertNotNull(usuario.getDocumento(), usuario.getUsuario());
            assertFalse(usuario.getDocumento().isBlank(), usuario.getUsuario());
        }
        assertEquals("admin", guardados.getAllValues().get(0).getUsuario());
        assertEquals("Administrador del sistema", guardados.getAllValues().get(0).getNombreCompleto());
    }

    @Test
    void losUsuariosDeDemostracionTienenUnDocumentoDistintoPorRol() {
        java.util.Set<String> documentos = new java.util.HashSet<>();
        for (Rol rol : Rol.values()) {
            assertTrue(documentos.add(DatosIniciales.documentoDemo(rol)), rol.name());
        }
        assertEquals("Demo Portero", DatosIniciales.nombreDemo(Rol.PORTERO));
    }

    @Test
    void alArrancarSeCompletanElAdministradorYLosDemoQueNoTenianDatos() {
        Usuario admin = new Usuario("admin", "x", Rol.ADMIN_SISTEMA, true);
        Usuario portero = new Usuario("demo_portero", "x", Rol.PORTERO, true);
        Usuario otro = new Usuario("portero1", "x", Rol.PORTERO, true);
        Usuario conDatos = new Usuario("demo_contador", "x", Rol.CONTADOR, true);
        conDatos.setNombreCompleto("Marta Gómez");
        conDatos.setDocumento("1002003004");
        when(repositorio.count()).thenReturn(4L);
        when(repositorio.findAll()).thenReturn(List.of(admin, portero, otro, conDatos));

        new DatosIniciales(repositorio, codificador, "ClaveAdmin2026", false, "").run();

        assertEquals("Administrador del sistema", admin.getNombreCompleto());
        assertNotNull(admin.getDocumento());
        assertEquals("Demo Portero", portero.getNombreCompleto());
        assertNotNull(portero.getDocumento());
        assertEquals(null, otro.getNombreCompleto());
        assertEquals("Marta Gómez", conDatos.getNombreCompleto());
        verify(repositorio, times(2)).save(any(Usuario.class));
    }
}
