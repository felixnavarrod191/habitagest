package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * NFR-PH-001 (T121): el estado de cuenta de un propietario responde en menos de 1 segundo con los 120 apartamentos
 * del conjunto, cada uno con 12 cuotas y 6 pagos.
 */
class RendimientoEstadoDeCuentaTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final int APARTAMENTOS = 120;
    private static final long LIMITE_MILISEGUNDOS = 1000;

    private final Clock reloj = Clock.fixed(Instant.parse("2026-11-12T15:00:00Z"), ZONA);
    private final RepositorioCuotas cuotas = RepositorioEnMemoria.crear(RepositorioCuotas.class);
    private final RepositorioPagos pagos = RepositorioEnMemoria.crear(RepositorioPagos.class);
    private final RepositorioAuditoria registros = RepositorioEnMemoria.crear(RepositorioAuditoria.class);
    private final List<ApartamentoRemoto> apartamentos = new ArrayList<>();
    private final ServicioPagos servicio = new ServicioPagos(cuotas, pagos, clientePrueba(),
            new ServicioAuditoria(registros, reloj), reloj);

    private ClienteCopropiedad clientePrueba() {
        return new ClienteCopropiedad() {
            @Override
            public List<ApartamentoRemoto> apartamentos(String torre) {
                return apartamentos;
            }

            @Override
            public Optional<ApartamentoRemoto> apartamento(String id) {
                return apartamentos.stream().filter(a -> a.id().equals(id)).findFirst();
            }
        };
    }

    private void cargarElConjunto() {
        for (int i = 1; i <= APARTAMENTOS; i++) {
            String id = "ap" + i;
            apartamentos.add(new ApartamentoRemoto(id, String.valueOf(1 + (i - 1) / 40), String.valueOf(100 + i),
                    100.0 / APARTAMENTOS));
            for (int mes = 1; mes <= 12; mes++) {
                Cuota cuota = cuotas.save(new Cuota(id, YearMonth.of(2026, mes).toString(), 150_000,
                        LocalDate.of(2026, mes, 10)));
                if (mes <= 6) {
                    servicio.registrarPago(cuota.getId(), 150_000, "contador1");
                }
            }
        }
    }

    @Test
    void elEstadoDeCuentaRespondeEnMenosDeUnSegundoConLos120Apartamentos() {
        cargarElConjunto();
        servicio.estadoDeCuenta("ap60");

        long inicio = System.nanoTime();
        EstadoCuenta estado = servicio.estadoDeCuenta("ap120");
        long milisegundos = (System.nanoTime() - inicio) / 1_000_000;

        assertEquals(12, estado.cuotas().size());
        assertEquals(6, estado.pagos().size());
        assertEquals(6 * 150_000, estado.saldo(), 0.001);
        assertTrue(milisegundos < LIMITE_MILISEGUNDOS,
                "El estado de cuenta tardó " + milisegundos + " ms; el límite es " + LIMITE_MILISEGUNDOS + " ms.");
    }

    @Test
    void consultarElEstadoDeCuentaDeLos120ApartamentosSeguidosTambienEsRapido() {
        cargarElConjunto();

        long inicio = System.nanoTime();
        for (ApartamentoRemoto apartamento : apartamentos) {
            servicio.estadoDeCuenta(apartamento.id());
        }
        long milisegundos = (System.nanoTime() - inicio) / 1_000_000;

        assertTrue(milisegundos < 10 * LIMITE_MILISEGUNDOS,
                "Las 120 consultas tardaron " + milisegundos + " ms.");
    }
}
