package com.habitagest.mantenimiento;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-030 y REQ-PH-038 a REQ-PH-042 (AC-030.1 y AC-038.1 a AC-042.1). */
class ServicioOrdenesTest {

    private final RepositorioAuditoria registros = RepositorioEnMemoria.crear(RepositorioAuditoria.class);
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-19T15:00:00Z"), ZoneId.of("America/Bogota"));
    private final ServicioOrdenes servicio = new ServicioOrdenes(RepositorioEnMemoria.crear(RepositorioOrdenes.class),
            new ServicioAuditoria(registros, reloj), reloj);

    private Orden orden(long costo, String proveedor) {
        return servicio.crear("Reparar el daño en la zona", "Piscina", costo, proveedor, "admin");
    }

    /** AC-038.1: el administrador crea la orden de una zona dañada y queda Solicitada. */
    @Test
    void laOrdenSeCreaSolicitada() {
        Orden orden = orden(800_000, null);

        assertEquals(Orden.SOLICITADA, orden.getEstado());
        assertEquals("Piscina", orden.getZona());
        assertEquals(800_000, orden.getCostoEstimado());
    }

    @Test
    void crearExigeDescripcionZonaYCostoPositivo() {
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crear(" ", "Piscina", 1L, null, "a")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crear("Daño", " ", 1L, null, "a")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crear("Daño", "Piscina", 0L, null, "a")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crear("Daño", "Piscina", null, null, "a")).estado());
    }

    /** AC-039.1: una orden de $800.000 la aprueba el administrador y pasa a Aprobada. */
    @Test
    void unaOrdenBajoElTopeLaApruebaElAdministrador() {
        Orden orden = orden(800_000, null);

        Orden aprobada = servicio.aprobar(orden.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin");

        assertEquals(Orden.APROBADA, aprobada.getEstado());
        assertEquals("ADMIN_CONJUNTO", aprobada.getAprobadaPor());
    }

    /** AC-040.1: una orden de $5.000.000 exige al Consejo y el administrador no la aprueba. */
    @Test
    void unaOrdenSobreElTopeSoloLaApruebaElConsejo() {
        Orden orden = orden(5_000_000, null);

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.aprobar(orden.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin"));

        assertEquals(403, error.estado());
        assertEquals(Orden.SOLICITADA, servicio.consultar(orden.getId()).getEstado());
        assertEquals(Orden.APROBADA, servicio.aprobar(orden.getId(), AprobadoPor.CONSEJO, "consejo1").getEstado());
    }

    @Test
    void elTopeExactoLoApruebaElAdministradorYSoloSeApruebaUnaVez() {
        Orden exacta = orden(2_000_000, null);

        servicio.aprobar(exacta.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin");

        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.aprobar(exacta.getId(), AprobadoPor.CONSEJO, "c")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.aprobar(exacta.getId(), null, "c")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class,
                () -> servicio.aprobar("no-existe", AprobadoPor.CONSEJO, "c")).estado());
    }

    /** AC-041.1: una orden Aprobada se ejecuta con materiales por $80.000 y queda Completada con ese costo. */
    @Test
    void ejecutarUnaOrdenAprobadaLaDejaCompletadaConSuCosto() {
        Orden orden = orden(800_000, null);
        servicio.aprobar(orden.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin");

        Orden completada = servicio.ejecutar(orden.getId(),
                List.of(new Material("Sellante", 50_000), new Material("Brocha", 30_000)), "encargado1");

        assertEquals(Orden.COMPLETADA, completada.getEstado());
        assertEquals(80_000L, completada.getCostoFinal());
        assertEquals(2, completada.getMateriales().size());
        assertNotNull(completada.getCompletada());
    }

    @Test
    void soloSeEjecutaUnaOrdenAprobadaConMaterialesValidos() {
        Orden orden = orden(800_000, null);

        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.ejecutar(orden.getId(), List.of(new Material("Sellante", 1_000)), "e")).estado());
        servicio.aprobar(orden.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin");
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.ejecutar(orden.getId(), List.of(), "e")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.ejecutar(orden.getId(), List.of(new Material("Sellante", 0)), "e")).estado());
    }

    /** AC-042.1: un proveedor con 2 órdenes asignadas de 5 existentes ve solo esas 2. */
    @Test
    void elProveedorVeSoloSusOrdenes() {
        orden(100_000, "proveedor1");
        orden(200_000, "proveedor1");
        orden(300_000, "proveedor2");
        orden(400_000, null);
        orden(500_000, "proveedor3");

        List<Orden> suyas = servicio.listar(null, "proveedor1");

        assertEquals(2, suyas.size());
        assertEquals(5, servicio.listar(null, null).size());
        assertEquals(5, servicio.listar("SOLICITADA", null).size());
        assertEquals(0, servicio.listar("APROBADA", null).size());
    }

    /** AC-030.1: los cambios de estado de la orden quedan auditados con usuario y fecha-hora. */
    @Test
    void cadaCambioDeEstadoQuedaAuditado() {
        Orden orden = orden(800_000, null);
        servicio.aprobar(orden.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin1");

        List<Cambio> cambios = servicio.auditoria(orden.getId());

        assertEquals(1, cambios.size());
        assertEquals("admin1", cambios.get(0).getUsuario());
        assertEquals(Orden.SOLICITADA, cambios.get(0).getEstadoAnterior());
        assertEquals(Orden.APROBADA, cambios.get(0).getEstadoNuevo());
        assertNotNull(cambios.get(0).getFecha());
    }

    @Test
    void losGastosDelMesSumanLasOrdenesCompletadas() {
        Orden primera = orden(800_000, null);
        servicio.aprobar(primera.getId(), AprobadoPor.ADMIN_CONJUNTO, "admin");
        servicio.ejecutar(primera.getId(), List.of(new Material("Sellante", 80_000)), "e");
        orden(100_000, null);

        assertEquals(80_000L, servicio.gastos("2026-11").total());
        assertEquals(0L, servicio.gastos("2026-10").total());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.gastos("noviembre")).estado());
    }
}
