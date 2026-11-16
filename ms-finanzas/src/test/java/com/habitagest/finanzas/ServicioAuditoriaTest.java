package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** NFR-PH-003: el cambio de estado queda registrado con el usuario y la fecha-hora. */
class ServicioAuditoriaTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-16T15:30:00Z"), ZONA);
    private final RepositorioAuditoria repositorio = RepositorioEnMemoria.crear(RepositorioAuditoria.class);
    private final ServicioAuditoria auditoria = new ServicioAuditoria(repositorio, reloj);

    @Test
    void registraElCambioDeEstadoConSuUsuarioYSuFecha() {
        auditoria.registrar("Cuota", "c1", "PENDIENTE", "PAGADA", "contador1");
        auditoria.registrar("Cuota", "c2", "PENDIENTE", "VENCIDA", null);

        List<RegistroAuditoria> registros = repositorio.findAll();
        assertEquals(2, registros.size());
        assertEquals("contador1", registros.get(0).getUsuario());
        assertEquals("PENDIENTE", registros.get(0).getEstadoAnterior());
        assertEquals("PAGADA", registros.get(0).getEstadoNuevo());
        assertEquals(LocalDateTime.of(2026, 11, 16, 10, 30), registros.get(0).getFecha());
        assertEquals("sistema", registros.get(1).getUsuario());
    }
}
