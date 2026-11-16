package com.habitagest.finanzas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-011 a REQ-PH-016: pagos, mora, cartera y estado de cuenta. */
@Service
public class ServicioPagos {

    /** Parámetro del negocio: interés de mora de 2 % mensual sobre el saldo vencido. */
    static final BigDecimal INTERES_DE_MORA = new BigDecimal("0.02");

    private final RepositorioCuotas cuotas;
    private final RepositorioPagos pagos;
    private final ClienteCopropiedad copropiedad;
    private final ServicioAuditoria auditoria;
    private final Clock reloj;

    public ServicioPagos(RepositorioCuotas cuotas, RepositorioPagos pagos, ClienteCopropiedad copropiedad,
            ServicioAuditoria auditoria, Clock reloj) {
        this.cuotas = cuotas;
        this.pagos = pagos;
        this.copropiedad = copropiedad;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** REQ-PH-011 y REQ-PH-012: descuenta el pago del saldo, sin permitir que lo supere. */
    public CuotaVista registrarPago(String cuotaId, double valor, String usuario) {
        Cuota cuota = cuotas.findById(cuotaId)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe la cuota " + cuotaId + "."));
        if (valor <= 0) {
            throw ErrorDeNegocio.solicitudInvalida("El valor del pago debe ser mayor que 0.");
        }
        BigDecimal saldo = BigDecimal.valueOf(cuota.getSaldo());
        BigDecimal pago = BigDecimal.valueOf(valor);
        if (pago.compareTo(saldo) > 0) {
            throw ErrorDeNegocio.reglaDeNegocio(
                    "El pago supera el saldo pendiente de la cuota: el saldo es $" + Pesos.formato(saldo) + ".");
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        String estadoAnterior = CuotaVista.estadoDe(cuota, ahora.toLocalDate());
        cuota.setSaldo(saldo.subtract(pago).doubleValue());
        cuotas.save(cuota);
        pagos.save(new Pago(cuota.getId(), cuota.getApartamentoId(), valor, ahora, usuario));
        String estadoNuevo = CuotaVista.estadoDe(cuota, ahora.toLocalDate());
        auditoria.registrar("Cuota", cuota.getId(), estadoAnterior, estadoNuevo, usuario);
        return CuotaVista.de(cuota, ahora.toLocalDate());
    }

    /**
     * REQ-PH-013: suma a cada cuota vencida el interés de 2 % sobre su saldo. Una cuota recibe la mora una sola
     * vez por mes calendario, para que repetir la acción no la duplique.
     */
    public List<CuotaVista> aplicarMora(String mes) {
        LocalDate hoy = LocalDate.now(reloj);
        String referencia = YearMonth.from(hoy).toString();
        String filtro = (mes == null || mes.isBlank()) ? null : ServicioCuotas.periodo(mes).toString();
        List<CuotaVista> afectadas = new ArrayList<>();
        for (Cuota cuota : cuotas.findAll()) {
            boolean vencida = cuota.getSaldo() > 0 && hoy.isAfter(cuota.getVencimiento());
            boolean delMes = filtro == null || filtro.equals(cuota.getMes());
            if (vencida && delMes && !referencia.equals(cuota.getMoraAplicadaMes())) {
                BigDecimal saldo = BigDecimal.valueOf(cuota.getSaldo());
                BigDecimal interes = saldo.multiply(INTERES_DE_MORA).setScale(2, RoundingMode.HALF_UP);
                cuota.setMora(BigDecimal.valueOf(cuota.getMora()).add(interes).doubleValue());
                cuota.setSaldo(saldo.add(interes).doubleValue());
                cuota.setMoraAplicadaMes(referencia);
                cuotas.save(cuota);
                afectadas.add(CuotaVista.de(cuota, hoy));
            }
        }
        return afectadas;
    }

    /** REQ-PH-014: un apartamento con una cuota vencida está en mora. */
    public EstadoMora estadoMora(String apartamentoId) {
        copropiedad.apartamento(apartamentoId)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe el apartamento " + apartamentoId + "."));
        LocalDate hoy = LocalDate.now(reloj);
        BigDecimal vencido = BigDecimal.ZERO;
        for (Cuota cuota : cuotas.findByApartamentoId(apartamentoId)) {
            if (cuota.getSaldo() > 0 && hoy.isAfter(cuota.getVencimiento())) {
                vencido = vencido.add(BigDecimal.valueOf(cuota.getSaldo()));
            }
        }
        return new EstadoMora(apartamentoId, vencido.signum() > 0, vencido.doubleValue());
    }

    /** REQ-PH-016: la cartera con el saldo de cada apartamento; con torre, solo los de esa torre. */
    public List<CarteraItem> cartera(String torre) {
        LocalDate hoy = LocalDate.now(reloj);
        List<CarteraItem> items = new ArrayList<>();
        for (ApartamentoRemoto apartamento : copropiedad.apartamentos(torre)) {
            BigDecimal saldo = BigDecimal.ZERO;
            boolean enMora = false;
            for (Cuota cuota : cuotas.findByApartamentoId(apartamento.id())) {
                saldo = saldo.add(BigDecimal.valueOf(cuota.getSaldo()));
                if (cuota.getSaldo() > 0 && hoy.isAfter(cuota.getVencimiento())) {
                    enMora = true;
                }
            }
            items.add(new CarteraItem(apartamento.id(), apartamento.torre(), apartamento.numero(), saldo.doubleValue(),
                    enMora));
        }
        return items;
    }

    /** REQ-PH-015: las cuotas, los pagos, el saldo y la mora de un apartamento. */
    public EstadoCuenta estadoDeCuenta(String apartamentoId) {
        copropiedad.apartamento(apartamentoId)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe el apartamento " + apartamentoId + "."));
        LocalDate hoy = LocalDate.now(reloj);
        List<CuotaVista> vistas = new ArrayList<>();
        BigDecimal saldo = BigDecimal.ZERO;
        BigDecimal mora = BigDecimal.ZERO;
        for (Cuota cuota : cuotas.findByApartamentoId(apartamentoId)) {
            vistas.add(CuotaVista.de(cuota, hoy));
            saldo = saldo.add(BigDecimal.valueOf(cuota.getSaldo()));
            mora = mora.add(BigDecimal.valueOf(cuota.getMora()));
        }
        List<PagoVista> pagosVista = new ArrayList<>();
        for (Pago pago : pagos.findByApartamentoId(apartamentoId)) {
            pagosVista.add(new PagoVista(pago.getId(), pago.getCuotaId(), pago.getValor(), pago.getFecha()));
        }
        return new EstadoCuenta(apartamentoId, vistas, pagosVista, saldo.doubleValue(), mora.doubleValue());
    }
}
