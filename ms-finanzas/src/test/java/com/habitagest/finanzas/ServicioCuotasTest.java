package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-008 a REQ-PH-010 (AC-008.1, AC-009.1, AC-009.2 y AC-010.1). */
class ServicioCuotasTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-12T15:00:00Z"), ZONA);
    private final RepositorioCuotas cuotas = RepositorioEnMemoria.crear(RepositorioCuotas.class);

    /** Un ClienteCopropiedad de prueba con los apartamentos dados. */
    private ServicioCuotas servicioConApartamentos(double... coeficientes) {
        List<ApartamentoRemoto> lista = new java.util.ArrayList<>();
        for (int i = 0; i < coeficientes.length; i++) {
            lista.add(new ApartamentoRemoto("a" + (i + 1), "1", String.valueOf(301 + i), coeficientes[i]));
        }
        ClienteCopropiedad cliente = new ClienteCopropiedad() {
            @Override
            public List<ApartamentoRemoto> apartamentos(String torre) {
                return lista;
            }

            @Override
            public Optional<ApartamentoRemoto> apartamento(String id) {
                return lista.stream().filter(a -> a.id().equals(id)).findFirst();
            }
        };
        return new ServicioCuotas(RepositorioEnMemoria.crear(RepositorioPresupuestos.class), cuotas, cliente, reloj);
    }

    /** AC-008.1: un presupuesto de $10.000.000 para noviembre queda registrado para noviembre. */
    @Test
    void guardaElPresupuestoDelMes() {
        ServicioCuotas servicio = servicioConApartamentos(1.5);

        servicio.guardarPresupuesto("2026-11", 10_000_000);

        Presupuesto guardado = servicio.consultarPresupuesto("2026-11");
        assertEquals("2026-11", guardado.getMes());
        assertEquals(10_000_000, guardado.getValor());
    }

    @Test
    void noGuardaDosPresupuestosDelMismoMesNiValoresSinSentido() {
        ServicioCuotas servicio = servicioConApartamentos(1.5);
        servicio.guardarPresupuesto("2026-11", 10_000_000);

        assertEquals(409, assertThrows(ErrorDeNegocio.class, () -> servicio.guardarPresupuesto("2026-11", 1)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.guardarPresupuesto("2026-12", 0)).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.guardarPresupuesto("noviembre", 5)).estado());
    }

    /** AC-009.1: presupuesto de $10.000.000 y coeficiente de 1,5 %: la cuota es de $150.000. */
    @Test
    void laCuotaEsElPresupuestoPorElCoeficiente() {
        ServicioCuotas servicio = servicioConApartamentos(1.5);
        servicio.guardarPresupuesto("2026-12", 10_000_000);

        List<CuotaVista> generadas = servicio.generarCuotas("2026-12");

        assertEquals(1, generadas.size());
        assertEquals(150_000, generadas.get(0).valor());
        assertEquals(150_000, generadas.get(0).saldo());
        assertEquals("PENDIENTE", generadas.get(0).estado());
    }

    /** AC-009.2: con coeficientes que suman 100 %, la suma de las cuotas es el presupuesto. */
    @Test
    void conCoeficientesQueSuman100LaSumaDeLasCuotasEsElPresupuesto() {
        ServicioCuotas servicio = servicioConApartamentos(33.33, 33.33, 33.34);
        servicio.guardarPresupuesto("2026-11", 1000.01);

        List<CuotaVista> generadas = servicio.generarCuotas("2026-11");

        BigDecimal total = BigDecimal.ZERO;
        for (CuotaVista cuota : generadas) {
            total = total.add(BigDecimal.valueOf(cuota.valor()));
        }
        assertEquals(0, new BigDecimal("1000.01").compareTo(total));
    }

    @Test
    void elRepartoConDiezMillonesYCoeficientesExactosNoNecesitaAjuste() {
        List<BigDecimal> cuotasCalculadas = CalculoCuotas.repartir(new BigDecimal("10000000"),
                List.of(new BigDecimal("40"), new BigDecimal("35.5"), new BigDecimal("24.5")));

        assertEquals(0, new BigDecimal("4000000.00").compareTo(cuotasCalculadas.get(0)));
        assertEquals(0, new BigDecimal("3550000.00").compareTo(cuotasCalculadas.get(1)));
        assertEquals(0, new BigDecimal("2450000.00").compareTo(cuotasCalculadas.get(2)));
    }

    /** AC-010.1: un mes sin presupuesto no crea ninguna cuota e informa el motivo. */
    @Test
    void sinPresupuestoNoGeneraCuotasEInformaElMotivo() {
        ServicioCuotas servicio = servicioConApartamentos(1.5);

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class, () -> servicio.generarCuotas("2026-11"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("No hay presupuesto"));
        assertEquals(0, cuotas.findAll().size());
    }

    @Test
    void noGeneraLasCuotasDeUnMesDosVeces() {
        ServicioCuotas servicio = servicioConApartamentos(1.5);
        servicio.guardarPresupuesto("2026-11", 10_000_000);
        servicio.generarCuotas("2026-11");

        assertEquals(409, assertThrows(ErrorDeNegocio.class, () -> servicio.generarCuotas("2026-11")).estado());
        assertEquals(1, cuotas.findAll().size());
    }

    @Test
    void lasCuotasVencenElDiezYSeConsultanPorApartamentoYMes() {
        ServicioCuotas servicio = servicioConApartamentos(1.5, 2);
        servicio.guardarPresupuesto("2026-11", 10_000_000);
        servicio.generarCuotas("2026-11");

        assertEquals(LocalDate.of(2026, 11, 10), cuotas.findAll().get(0).getVencimiento());
        assertEquals(1, servicio.consultarCuotas("a1", "2026-11").size());
        assertEquals(2, servicio.consultarCuotas(null, "2026-11").size());
        assertEquals(0, servicio.consultarCuotas("a1", "2026-12").size());
    }

    @Test
    void despuesDelDiezUnaCuotaConSaldoSeMuestraVencida() {
        Cuota cuota = new Cuota("a1", "2026-11", 150_000, LocalDate.of(2026, 11, 10));

        assertEquals("PENDIENTE", CuotaVista.estadoDe(cuota, LocalDate.of(2026, 11, 10)));
        assertEquals("VENCIDA", CuotaVista.estadoDe(cuota, LocalDate.of(2026, 11, 11)));
        cuota.setSaldo(0);
        assertEquals("PAGADA", CuotaVista.estadoDe(cuota, LocalDate.of(2026, 11, 11)));
    }
}
