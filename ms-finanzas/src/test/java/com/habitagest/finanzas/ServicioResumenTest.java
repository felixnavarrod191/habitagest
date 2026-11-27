package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-043 (AC-043.1): el Consejo ve el total de cartera y de gastos del mes. */
class ServicioResumenTest {

    private final RepositorioCuotas cuotas = RepositorioEnMemoria.crear(RepositorioCuotas.class);
    private final List<String> mesesConsultados = new ArrayList<>();
    private final ClienteMantenimiento mantenimiento = mes -> {
        mesesConsultados.add(mes);
        return "2026-11".equals(mes) ? 80_000 : 0;
    };
    private final ServicioResumen servicio = new ServicioResumen(cuotas, mantenimiento);

    private void cuota(String apartamento, String mes, double valor, double saldo) {
        Cuota cuota = cuotas.save(new Cuota(apartamento, mes, valor, LocalDate.parse(mes + "-10")));
        cuota.setSaldo(saldo);
        cuotas.save(cuota);
    }

    /** AC-043.1: la cartera y los gastos del mes se muestran con su total por concepto. */
    @Test
    void elResumenTraeElTotalDeCarteraYElTotalDeGastosDelMes() {
        cuota("a1", "2026-11", 150_000, 150_000);
        cuota("a2", "2026-11", 150_000, 50_000);
        cuota("a3", "2026-11", 150_000, 0);

        ResumenFinanciero resumen = servicio.resumen("2026-11");

        assertEquals("2026-11", resumen.mes());
        assertEquals(200_000, resumen.totalCartera());
        assertEquals(80_000, resumen.gastosMantenimiento());
    }

    @Test
    void laCarteraSoloSumaLasCuotasDelMesConsultado() {
        cuota("a1", "2026-10", 150_000, 30_000);
        cuota("a1", "2026-11", 150_000, 150_000);

        assertEquals(150_000, servicio.resumen("2026-11").totalCartera());
        assertEquals(30_000, servicio.resumen("2026-10").totalCartera());
        assertEquals(0, servicio.resumen("2026-10").gastosMantenimiento());
    }

    @Test
    void unMesSinCuotasNiGastosDaCeros() {
        ResumenFinanciero resumen = servicio.resumen("2026-09");

        assertEquals(0, resumen.totalCartera());
        assertEquals(0, resumen.gastosMantenimiento());
    }

    @Test
    void rechazaUnMesConFormatoIncorrectoSinLlamarAMantenimiento() {
        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class, () -> servicio.resumen("noviembre"));

        assertEquals(400, error.estado());
        assertEquals(0, mesesConsultados.size());
    }

    @Test
    void sinMesElResumenSeRechaza() {
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.resumen(null)).estado());
    }
}
