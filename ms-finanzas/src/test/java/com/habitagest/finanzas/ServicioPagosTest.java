package com.habitagest.finanzas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-011 a REQ-PH-016 (AC-011.1 a AC-016.1). */
class ServicioPagosTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    /** 12 de noviembre de 2026: la cuota de noviembre (vence el 10) ya está vencida. */
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
                List<ApartamentoRemoto> filtrados = new ArrayList<>();
                for (ApartamentoRemoto apartamento : apartamentos) {
                    if (torre == null || torre.equals(apartamento.torre())) {
                        filtrados.add(apartamento);
                    }
                }
                return filtrados;
            }

            @Override
            public Optional<ApartamentoRemoto> apartamento(String id) {
                return apartamentos.stream().filter(a -> a.id().equals(id)).findFirst();
            }
        };
    }

    private Cuota cuotaVencida(String apartamentoId, String torre, double valor) {
        apartamentos.add(new ApartamentoRemoto(apartamentoId, torre, "301", 1.5));
        return cuotas.save(new Cuota(apartamentoId, "2026-11", valor, LocalDate.of(2026, 11, 10)));
    }

    /** AC-011.1: una cuota con saldo de $150.000 y un pago de $100.000 queda con saldo de $50.000. */
    @Test
    void elPagoSeDescuentaDelSaldo() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);

        CuotaVista resultado = servicio.registrarPago(cuota.getId(), 100_000, "contador1");

        assertEquals(50_000, resultado.saldo());
        assertEquals(50_000, cuotas.findById(cuota.getId()).get().getSaldo());
        assertEquals(1, pagos.findAll().size());
    }

    @Test
    void pagarTodoElSaldoDejaLaCuotaPagadaYQuedaAuditado() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);

        CuotaVista resultado = servicio.registrarPago(cuota.getId(), 150_000, "contador1");

        assertEquals("PAGADA", resultado.estado());
        assertEquals(1, registros.findAll().size());
        assertEquals("VENCIDA", registros.findAll().get(0).getEstadoAnterior());
        assertEquals("PAGADA", registros.findAll().get(0).getEstadoNuevo());
        assertEquals("contador1", registros.findAll().get(0).getUsuario());
    }

    /** AC-012.1: un pago de $200.000 sobre un saldo de $150.000 se rechaza e indica el saldo. */
    @Test
    void rechazaElPagoMayorAlSaldoEIndicaElSaldo() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.registrarPago(cuota.getId(), 200_000, "contador1"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("150.000"));
        assertEquals(150_000, cuotas.findById(cuota.getId()).get().getSaldo());
        assertEquals(0, pagos.findAll().size());
    }

    @Test
    void rechazaPagosSinSentidoOSobreUnaCuotaQueNoExiste() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);

        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.registrarPago(cuota.getId(), 0, "c")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.registrarPago("no-existe", 10, "c")).estado());
    }

    /** AC-013.1: una cuota de $150.000 vencida recibe $3.000 de interés al aplicar la mora. */
    @Test
    void laMoraSuma2PorCientoDelSaldoALaCuotaVencida() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);

        List<CuotaVista> afectadas = servicio.aplicarMora(null);

        assertEquals(1, afectadas.size());
        assertEquals(3_000, afectadas.get(0).mora());
        assertEquals(153_000, afectadas.get(0).saldo());
        assertEquals(153_000, cuotas.findById(cuota.getId()).get().getSaldo());
    }

    @Test
    void repetirLaMoraEnElMismoMesNoLaDuplica() {
        cuotaVencida("a1", "1", 150_000);
        cuotas.save(new Cuota("a2", "2026-12", 100_000, LocalDate.of(2026, 12, 10)));

        assertEquals(1, servicio.aplicarMora(null).size());
        assertEquals(0, servicio.aplicarMora(null).size());
    }

    @Test
    void laMoraPuedeLimitarseAUnMes() {
        cuotaVencida("a1", "1", 150_000);

        assertEquals(0, servicio.aplicarMora("2026-10").size());
        assertEquals(1, servicio.aplicarMora("2026-11").size());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.aplicarMora("noviembre")).estado());
    }

    /** AC-014.1: el apartamento 301 con una cuota vencida aparece en mora en la cartera. */
    @Test
    void elApartamentoConCuotaVencidaApareceEnMora() {
        cuotaVencida("a1", "1", 150_000);

        List<CarteraItem> cartera = servicio.cartera(null);

        assertEquals(1, cartera.size());
        assertTrue(cartera.get(0).enMora());
        assertTrue(servicio.estadoMora("a1").enMora());
        assertEquals(150_000, servicio.estadoMora("a1").saldoVencido());
    }

    @Test
    void despuesDePagarTodoElApartamentoYaNoEstaEnMora() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);
        servicio.registrarPago(cuota.getId(), 150_000, "contador1");

        assertFalse(servicio.estadoMora("a1").enMora());
    }

    @Test
    void elEstadoDeMoraDeUnApartamentoInexistenteDa404() {
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.estadoMora("no-existe")).estado());
    }

    /** AC-015.1: una cuota vencida de $150.000 con $3.000 de mora muestra saldo de $153.000 y mora de $3.000. */
    @Test
    void elEstadoDeCuentaMuestraSaldoYMora() {
        cuotaVencida("a1", "1", 150_000);
        servicio.aplicarMora(null);

        EstadoCuenta estado = servicio.estadoDeCuenta("a1");

        assertEquals(153_000, estado.saldo());
        assertEquals(3_000, estado.mora());
        assertEquals(1, estado.cuotas().size());
        assertEquals(0, estado.pagos().size());
    }

    @Test
    void elEstadoDeCuentaIncluyeLosPagos() {
        Cuota cuota = cuotaVencida("a1", "1", 150_000);
        servicio.registrarPago(cuota.getId(), 50_000, "contador1");

        EstadoCuenta estado = servicio.estadoDeCuenta("a1");

        assertEquals(100_000, estado.saldo());
        assertEquals(1, estado.pagos().size());
        assertEquals(50_000, estado.pagos().get(0).valor());
    }

    /** AC-016.1: con apartamentos con saldo en las torres 1 y 2, filtrar por la torre 1 muestra solo los de la 1. */
    @Test
    void laCarteraSeFiltraPorTorre() {
        cuotaVencida("a1", "1", 150_000);
        cuotaVencida("a2", "2", 200_000);

        List<CarteraItem> torreUno = servicio.cartera("1");

        assertEquals(1, torreUno.size());
        assertEquals("a1", torreUno.get(0).apartamentoId());
        assertEquals(150_000, torreUno.get(0).saldo());
        assertEquals(2, servicio.cartera(null).size());
    }

    @Test
    void elFormatoDePesosUsaPuntoParaLosMiles() {
        assertEquals("150.000", Pesos.formato(new java.math.BigDecimal("150000")));
        assertEquals("1.250.000,5", Pesos.formato(new java.math.BigDecimal("1250000.5")));
    }
}
