package com.habitagest.pqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-031 y REQ-PH-032 (AC-031.1 y AC-032.1). */
class ServicioComunicadosTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private final RepositorioComunicados repositorio = RepositorioEnMemoria.crear(RepositorioComunicados.class);

    private ServicioComunicados servicioEl(String instanteUtc) {
        return new ServicioComunicados(repositorio, Clock.fixed(Instant.parse(instanteUtc), ZONA));
    }

    /** AC-031.1: un comunicado vigente lo ve el residente. */
    @Test
    void unComunicadoVigenteSeMuestra() {
        ServicioComunicados servicio = servicioEl("2026-11-19T15:00:00Z");
        servicio.publicar("Corte de agua el sábado", "Habrá corte de 8:00 a 12:00.", LocalDate.of(2026, 11, 22));

        List<Comunicado> vigentes = servicio.vigentes();

        assertEquals(1, vigentes.size());
        assertEquals("Corte de agua el sábado", vigentes.get(0).getTitulo());
    }

    /** AC-032.1: un comunicado vencido ya no aparece. */
    @Test
    void unComunicadoVencidoNoSeMuestra() {
        servicioEl("2026-11-19T15:00:00Z").publicar("Reunión", "Asamblea.", LocalDate.of(2026, 11, 20));

        assertEquals(0, servicioEl("2026-11-21T15:00:00Z").vigentes().size());
        assertEquals(1, servicioEl("2026-11-20T15:00:00Z").vigentes().size());
    }

    @Test
    void publicarSinDatosOConVigenciaPasadaSeRechaza() {
        ServicioComunicados servicio = servicioEl("2026-11-19T15:00:00Z");

        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.publicar(" ", "texto", LocalDate.of(2026, 11, 25))).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.publicar("Título", "texto", null)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.publicar("Título", "texto", LocalDate.of(2026, 11, 18))).estado());
    }
}
