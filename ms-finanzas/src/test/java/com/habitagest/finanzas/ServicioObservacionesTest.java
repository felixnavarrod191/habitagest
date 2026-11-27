package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-044 (AC-044.1): una observación del revisor fiscal la ve el administrador con fecha y autor. */
class ServicioObservacionesTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private final RepositorioObservaciones repositorio = RepositorioEnMemoria.crear(RepositorioObservaciones.class);
    private Clock reloj = Clock.fixed(Instant.parse("2026-11-26T15:00:00Z"), ZONA);

    private ServicioObservaciones servicio() {
        return new ServicioObservaciones(repositorio, reloj);
    }

    /** AC-044.1: la observación del revisor fiscal queda con su autor y su fecha y la ve el administrador. */
    @Test
    void laObservacionDelRevisorLaVeElAdministradorConFechaYAutor() {
        servicio().dejar("Pago p-15", "El pago no tiene soporte.", "revisor1", "REVISOR_FISCAL");

        List<Observacion> vistas = servicio().listar("ADMIN_CONJUNTO");

        assertEquals(1, vistas.size());
        assertEquals("Pago p-15", vistas.get(0).getReferencia());
        assertEquals("El pago no tiene soporte.", vistas.get(0).getTexto());
        assertEquals("revisor1", vistas.get(0).getAutor());
        assertEquals(LocalDateTime.of(2026, 11, 26, 10, 0), vistas.get(0).getFecha());
    }

    @Test
    void soloElRevisorFiscalPuedeDejarObservaciones() {
        for (String rol : new String[] {"ADMIN_CONJUNTO", "CONTADOR", "CONSEJO", "RESIDENTE", null}) {
            ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                    () -> servicio().dejar("Pago p-1", "Texto", "alguien", rol));
            assertEquals(403, error.estado());
        }
        assertEquals(0, repositorio.findAll().size());
    }

    @Test
    void rechazaObservacionesSinReferenciaSinTextoOSinAutor() {
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio().dejar("  ", "Texto", "revisor1", "REVISOR_FISCAL")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio().dejar("Pago p-1", "", "revisor1", "REVISOR_FISCAL")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio().dejar("Pago p-1", "Texto", " ", "REVISOR_FISCAL")).estado());
        assertEquals(0, repositorio.findAll().size());
    }

    @Test
    void laListaMuestraPrimeroLaObservacionMasReciente() {
        servicio().dejar("Pago p-1", "Primera", "revisor1", "REVISOR_FISCAL");
        reloj = Clock.fixed(Instant.parse("2026-11-27T15:00:00Z"), ZONA);
        servicio().dejar("Orden o-2", "Segunda", "revisor1", "REVISOR_FISCAL");

        List<Observacion> vistas = servicio().listar("REVISOR_FISCAL");

        assertEquals("Segunda", vistas.get(0).getTexto());
        assertEquals("Primera", vistas.get(1).getTexto());
    }

    @Test
    void soloLosRolesAutorizadosConsultanLasObservaciones() {
        servicio().dejar("Pago p-1", "Texto", "revisor1", "REVISOR_FISCAL");

        assertTrue(servicio().listar("REVISOR_FISCAL").size() == 1);
        assertTrue(servicio().listar("ADMIN_CONJUNTO").size() == 1);
        for (String rol : new String[] {"CONSEJO", "CONTADOR", "PROPIETARIO", "PROVEEDOR", null}) {
            assertEquals(403, assertThrows(ErrorDeNegocio.class, () -> servicio().listar(rol)).estado());
        }
    }
}
