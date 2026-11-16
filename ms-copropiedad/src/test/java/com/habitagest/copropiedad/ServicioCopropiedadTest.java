package com.habitagest.copropiedad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-004 a REQ-PH-007 (AC-004.1, AC-005.1, AC-006.1 y AC-007.1). */
class ServicioCopropiedadTest {

    private final RepositorioApartamentos apartamentos = RepositorioEnMemoria.crear(RepositorioApartamentos.class);
    private final RepositorioPropietarios propietarios = RepositorioEnMemoria.crear(RepositorioPropietarios.class);
    private final RepositorioResidentes residentes = RepositorioEnMemoria.crear(RepositorioResidentes.class);
    private final ServicioCopropiedad servicio = new ServicioCopropiedad(
            RepositorioEnMemoria.crear(RepositorioTorres.class), apartamentos, propietarios, residentes);

    /** AC-004.1: la torre 1 y el apartamento 301 con coeficiente de 1,5 % quedan guardados. */
    @Test
    void registraElApartamentoEnSuTorreConSuCoeficiente() {
        servicio.crearTorre("1");

        Apartamento creado = servicio.crearApartamento("1", "301", 1.5);

        Apartamento guardado = servicio.obtenerApartamento(creado.getId());
        assertEquals("1", guardado.getTorre());
        assertEquals("301", guardado.getNumero());
        assertEquals(1.5, guardado.getCoeficiente());
    }

    /** AC-005.1: con coeficientes que suman 99 %, un apartamento de 2 % se rechaza e indica 101 %. */
    @Test
    void rechazaElApartamentoQueHaceQueLaSumaSupereElCienPorCiento() {
        servicio.crearTorre("1");
        servicio.crearApartamento("1", "101", 99);

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class, () -> servicio.crearApartamento("1", "302", 2));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("101 %"));
        assertEquals(1, servicio.listarApartamentos(null).size());
    }

    @Test
    void permiteQueLosCoeficientesSumenExactamenteCien() {
        servicio.crearTorre("1");
        servicio.crearApartamento("1", "101", 99.5);
        servicio.crearApartamento("1", "102", 0.5);

        assertEquals(2, servicio.listarApartamentos(null).size());
    }

    @Test
    void rechazaUnCoeficienteSinSentidoOUnaTorreQueNoExiste() {
        servicio.crearTorre("1");

        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crearApartamento("1", "101", 0)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crearApartamento("9", "101", 1)).estado());
    }

    @Test
    void rechazaUnApartamentoRepetidoEnLaMismaTorre() {
        servicio.crearTorre("1");
        servicio.crearApartamento("1", "301", 1.5);

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class, () -> servicio.crearApartamento("1", "301", 1));

        assertEquals(409, error.estado());
    }

    @Test
    void filtraLosApartamentosPorTorre() {
        servicio.crearTorre("1");
        servicio.crearTorre("2");
        servicio.crearApartamento("1", "301", 1.5);
        servicio.crearApartamento("2", "301", 1.5);

        assertEquals(1, servicio.listarApartamentos("1").size());
        assertEquals(2, servicio.listarApartamentos(null).size());
    }

    /** AC-006.1: el propietario del 301 queda asociado al 301 y no al 302. */
    @Test
    void asociaAlPropietarioSoloConSuApartamento() {
        servicio.crearTorre("1");
        Apartamento a301 = servicio.crearApartamento("1", "301", 1.5);
        Apartamento a302 = servicio.crearApartamento("1", "302", 1.5);

        Propietario propietario = servicio.crearPropietario("Marta Gómez", "1001", a301.getId());

        assertEquals(a301.getId(), servicio.obtenerPropietario(propietario.getId()).getApartamentoId());
        assertNotEquals(a302.getId(), servicio.obtenerPropietario(propietario.getId()).getApartamentoId());
        assertEquals(propietario.getId(), servicio.obtenerApartamento(a301.getId()).getPropietarioId());
        assertNull(servicio.obtenerApartamento(a302.getId()).getPropietarioId());
    }

    @Test
    void cadaApartamentoTieneUnSoloPropietarioResponsable() {
        servicio.crearTorre("1");
        Apartamento a301 = servicio.crearApartamento("1", "301", 1.5);
        servicio.crearPropietario("Marta Gómez", "1001", a301.getId());

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.crearPropietario("Otro Dueño", "1002", a301.getId()));

        assertEquals(409, error.estado());
    }

    @Test
    void noRegistraPropietariosNiResidentesEnUnApartamentoQueNoExiste() {
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.crearPropietario("A", "1", "x")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.crearResidente("A", "1", "x")).estado());
    }

    /** AC-007.1: un apartamento con dos residentes registrados muestra a los dos. */
    @Test
    void muestraLosDosResidentesDelApartamento() {
        servicio.crearTorre("1");
        Apartamento a301 = servicio.crearApartamento("1", "301", 1.5);
        Apartamento a302 = servicio.crearApartamento("1", "302", 1.5);
        servicio.crearResidente("Luis Pérez", "2001", a301.getId());
        servicio.crearResidente("Ana Ruiz", "2002", a301.getId());
        servicio.crearResidente("Otro Vecino", "2003", a302.getId());

        List<Residente> delTrescientosUno = servicio.listarResidentes(a301.getId());

        assertEquals(2, delTrescientosUno.size());
        assertEquals(1, servicio.listarResidentes(a302.getId()).size());
    }

    @Test
    void noRepiteElNombreDeUnaTorre() {
        servicio.crearTorre("1");

        assertEquals(409, assertThrows(ErrorDeNegocio.class, () -> servicio.crearTorre("1")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crearTorre(" ")).estado());
    }
}
