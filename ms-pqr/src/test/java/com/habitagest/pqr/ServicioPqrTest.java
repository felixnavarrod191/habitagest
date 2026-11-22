package com.habitagest.pqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-025 a REQ-PH-030 (AC-025.1 a AC-030.1). */
class ServicioPqrTest {

    private final RepositorioAuditoria registros = RepositorioEnMemoria.crear(RepositorioAuditoria.class);
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-19T15:00:00Z"), ZoneId.of("America/Bogota"));
    private final ServicioPqr servicio = new ServicioPqr(RepositorioEnMemoria.crear(RepositorioPqr.class),
            new ServicioAuditoria(registros, reloj), reloj);

    private Pqr gotera() {
        return servicio.radicar("residente204", "Gotera en el baño", "Hay una gotera en el techo del baño.", "residente204");
    }

    /** AC-025.1: la PQR queda Radicada con número, fecha y autor. */
    @Test
    void radicarDejaLaPqrRadicadaConNumeroFechaYAutor() {
        Pqr pqr = gotera();

        assertEquals(Pqr.RADICADA, pqr.getEstado());
        assertEquals("PQR-0001", pqr.getNumero());
        assertEquals("residente204", pqr.getResidenteId());
        assertNotNull(pqr.getFecha());
        assertEquals(1, registros.findAll().size());
    }

    @Test
    void radicarSinDatosSeRechazaYLosNumerosSonConsecutivos() {
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.radicar("r1", " ", "texto", "r1")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.radicar(null, "a", "texto", "r1")).estado());
        gotera();
        assertEquals("PQR-0002", gotera().getNumero());
    }

    /** AC-026.1: una PQR Radicada pasa a En revisión y el historial conserva el cambio. */
    @Test
    void laPqrAvanzaAlEstadoSiguienteYConservaElHistorial() {
        Pqr pqr = gotera();

        String trasLaRevision = servicio.cambiarEstado(pqr.getId(), Pqr.EN_REVISION, "admin").getEstado();
        Pqr enGestion = servicio.cambiarEstado(pqr.getId(), Pqr.EN_GESTION, "admin");

        assertEquals(Pqr.EN_REVISION, trasLaRevision);
        assertEquals(Pqr.EN_GESTION, enGestion.getEstado());
        assertEquals(2, enGestion.getHistorial().size());
        assertEquals(Pqr.RADICADA, enGestion.getHistorial().get(0).getEstadoAnterior());
        assertEquals(Pqr.EN_REVISION, enGestion.getHistorial().get(0).getEstadoNuevo());
    }

    /** AC-027.1: no se puede saltar un estado y la PQR sigue como estaba. */
    @Test
    void unSaltoDeEstadoSeRechazaYLaPqrNoCambia() {
        Pqr pqr = gotera();

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.cambiarEstado(pqr.getId(), Pqr.RESUELTA, "admin"));

        assertEquals(409, error.estado());
        assertEquals(Pqr.RADICADA, servicio.consultar(pqr.getId()).getEstado());
        assertEquals(0, servicio.consultar(pqr.getId()).getHistorial().size());
    }

    @Test
    void unEstadoDesconocidoOElDeCierreNoSeAceptaComoCambio() {
        Pqr pqr = gotera();

        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cambiarEstado(pqr.getId(), "INVENTADO", "admin")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cambiarEstado(pqr.getId(), Pqr.CERRADA, "admin")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cambiarEstado("no-existe", Pqr.EN_REVISION, "admin")).estado());
    }

    /** AC-028.1: dos PQR de residentes distintos; cada uno ve solo la suya con su estado y su historial. */
    @Test
    void cadaResidenteVeSoloSusPqr() {
        Pqr suya = gotera();
        servicio.radicar("residente305", "Ruido", "Ruido en las noches.", "residente305");
        servicio.cambiarEstado(suya.getId(), Pqr.EN_REVISION, "admin");

        List<Pqr> deMaria = servicio.listar("residente204");

        assertEquals(1, deMaria.size());
        assertEquals("residente204", deMaria.get(0).getResidenteId());
        assertEquals(Pqr.EN_REVISION, deMaria.get(0).getEstado());
        assertEquals(1, deMaria.get(0).getHistorial().size());
        assertEquals(2, servicio.listar(null).size());
    }

    /** AC-029.1: una PQR Resuelta se cierra con una respuesta y después no se puede modificar. */
    @Test
    void unaPqrResueltaSeCierraConRespuestaYQuedaBloqueada() {
        Pqr pqr = gotera();
        servicio.cambiarEstado(pqr.getId(), Pqr.EN_REVISION, "admin");
        servicio.cambiarEstado(pqr.getId(), Pqr.EN_GESTION, "admin");
        servicio.cambiarEstado(pqr.getId(), Pqr.RESUELTA, "admin");

        Pqr cerrada = servicio.cerrar(pqr.getId(), "Se reparó la tubería.", "admin");

        assertEquals(Pqr.CERRADA, cerrada.getEstado());
        assertEquals("Se reparó la tubería.", cerrada.getRespuesta());
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cambiarEstado(pqr.getId(), Pqr.RESUELTA, "admin")).estado());
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cerrar(pqr.getId(), "Otra respuesta", "admin")).estado());
    }

    @Test
    void soloSeCierraUnaPqrResueltaYConRespuesta() {
        Pqr pqr = gotera();

        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.cerrar(pqr.getId(), "Respuesta", "admin")).estado());
        servicio.cambiarEstado(pqr.getId(), Pqr.EN_REVISION, "admin");
        servicio.cambiarEstado(pqr.getId(), Pqr.EN_GESTION, "admin");
        servicio.cambiarEstado(pqr.getId(), Pqr.RESUELTA, "admin");
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.cerrar(pqr.getId(), " ", "admin")).estado());
    }

    /** AC-030.1: el cambio a En revisión aparece en la auditoría con usuario y fecha-hora. */
    @Test
    void laAuditoriaGuardaUsuarioYFechaDeCadaCambio() {
        Pqr pqr = gotera();
        servicio.cambiarEstado(pqr.getId(), Pqr.EN_REVISION, "admin1");

        List<Cambio> cambios = servicio.auditoria(pqr.getId());

        assertEquals(1, cambios.size());
        assertEquals("admin1", cambios.get(0).getUsuario());
        assertNotNull(cambios.get(0).getFecha());
        assertTrue(registros.findAll().stream().anyMatch(registro -> "admin1".equals(registro.getUsuario())
                && Pqr.EN_REVISION.equals(registro.getEstadoNuevo())));
    }
}
