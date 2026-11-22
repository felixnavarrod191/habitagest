package com.habitagest.porteria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-033 a REQ-PH-037 (AC-033.1 a AC-037.1). */
class ServicioPorteriaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 19);
    private final Set<String> existentes = Set.of("a301", "a302");
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-19T20:00:00Z"), ZoneId.of("America/Bogota"));
    private final ServicioPorteria servicio = new ServicioPorteria(
            RepositorioEnMemoria.crear(RepositorioVisitas.class), RepositorioEnMemoria.crear(RepositorioIngresos.class),
            RepositorioEnMemoria.crear(RepositorioPaquetes.class), RepositorioEnMemoria.crear(RepositorioNovedades.class),
            existentes::contains,
            new ServicioAuditoria(RepositorioEnMemoria.crear(RepositorioAuditoria.class), reloj), reloj);

    private Visita carlos() {
        return servicio.autorizar("a301", "Carlos Pérez", "1010", HOY);
    }

    /** AC-033.1: una visita autorizada para hoy; el portero la ve como autorizada. */
    @Test
    void laVisitaAutorizadaApareceAutorizadaParaElPortero() {
        carlos();

        List<Visita> visitas = servicio.visitasDeLaFecha("2026-11-19");

        assertEquals(1, visitas.size());
        assertEquals("Carlos Pérez", visitas.get(0).getNombre());
        assertEquals("1010", visitas.get(0).getDocumento());
        assertEquals(Visita.AUTORIZADA, visitas.get(0).getEstado());
    }

    @Test
    void autorizarExigeDatosFechaVigenteYApartamentoExistente() {
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.autorizar("a301", " ", "1", HOY)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.autorizar("a301", "Ana", "1", HOY.minusDays(1))).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.autorizar("a999", "Ana", "1", HOY)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.visitasDeLaFecha("hoy")).estado());
    }

    /** AC-034.1: el ingreso y la salida de un visitante autorizado quedan guardados con la hora. */
    @Test
    void elIngresoYLaSalidaQuedanGuardadosConLaHora() {
        Visita visita = carlos();

        Ingreso entrada = servicio.registrar(visita.getId(), TipoIngreso.ENTRADA, "portero1");
        assertEquals("ENTRADA", entrada.getTipo());
        assertNotNull(entrada.getHora());
        assertEquals("portero1", entrada.getRegistradoPor());
        assertEquals(Visita.DENTRO, servicio.visitasDeLaFecha("2026-11-19").get(0).getEstado());

        Ingreso salida = servicio.registrar(visita.getId(), TipoIngreso.SALIDA, "portero1");
        assertEquals("SALIDA", salida.getTipo());
        assertEquals(Visita.SALIO, servicio.visitasDeLaFecha("2026-11-19").get(0).getEstado());
    }

    @Test
    void laSalidaExigeIngresoPreviosYNoSeRepiteElIngreso() {
        Visita visita = carlos();

        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.registrar(visita.getId(), TipoIngreso.SALIDA, "p")).estado());
        servicio.registrar(visita.getId(), TipoIngreso.ENTRADA, "p");
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.registrar(visita.getId(), TipoIngreso.ENTRADA, "p")).estado());
    }

    @Test
    void unaVisitaDeOtroDiaNoPuedeIngresarHoy() {
        Visita manana = servicio.autorizar("a302", "Luisa Gómez", "2020", HOY.plusDays(1));

        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.registrar(manana.getId(), TipoIngreso.ENTRADA, "p")).estado());
    }

    /** AC-035.1: un visitante que ningún residente autorizó no puede ingresar. */
    @Test
    void unVisitanteNoAutorizadoSeRechaza() {
        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.registrar("desconocido", TipoIngreso.ENTRADA, "portero1"));

        assertEquals(404, error.estado());
    }

    /** AC-036.1: un paquete registrado para el 301; el residente lo ve pendiente de recoger. */
    @Test
    void elPaqueteQuedaPendienteDeRecoger() {
        Paquete paquete = servicio.registrarPaquete("a301", "Caja de Mercado Libre");
        servicio.registrarPaquete("a302", "Sobre");

        List<Paquete> delTrescientosUno = servicio.paquetesDelApartamento("a301");

        assertEquals(1, delTrescientosUno.size());
        assertEquals(Paquete.PENDIENTE, delTrescientosUno.get(0).getEstado());
        assertEquals(Paquete.ENTREGADO, servicio.entregarPaquete(paquete.getId(), "portero1").getEstado());
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.entregarPaquete(paquete.getId(), "portero1")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.entregarPaquete("x", "p")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.registrarPaquete("a301", " ")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.paquetesDelApartamento(" ")).estado());
    }

    /** AC-037.1: la novedad registrada se consulta con fecha, hora y autor. */
    @Test
    void laNovedadGuardaFechaHoraYAutor() {
        servicio.registrarNovedad("Puerta del parqueadero sin seguro.", "vigilante1");

        List<Novedad> novedades = servicio.novedades();

        assertEquals(1, novedades.size());
        assertEquals("vigilante1", novedades.get(0).getAutor());
        assertNotNull(novedades.get(0).getFecha());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.registrarNovedad(" ", "v")).estado());
    }
}
