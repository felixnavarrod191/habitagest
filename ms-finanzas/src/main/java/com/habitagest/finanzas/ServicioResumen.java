package com.habitagest.finanzas;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.springframework.stereotype.Service;

/** REQ-PH-043: resumen de cartera y de gastos de mantenimiento de un mes para el Consejo. */
@Service
public class ServicioResumen {

    private final RepositorioCuotas cuotas;
    private final ClienteMantenimiento mantenimiento;

    public ServicioResumen(RepositorioCuotas cuotas, ClienteMantenimiento mantenimiento) {
        this.cuotas = cuotas;
        this.mantenimiento = mantenimiento;
    }

    /** Suma el saldo pendiente de las cuotas del mes (incluye la mora) y pide a mantenimiento sus gastos del mes. */
    public ResumenFinanciero resumen(String mes) {
        YearMonth periodo = ServicioCuotas.periodo(mes);
        BigDecimal cartera = BigDecimal.ZERO;
        for (Cuota cuota : cuotas.findByMes(periodo.toString())) {
            cartera = cartera.add(BigDecimal.valueOf(cuota.getSaldo()));
        }
        double gastos = mantenimiento.gastosDelMes(periodo.toString());
        return new ResumenFinanciero(periodo.toString(), cartera.doubleValue(), gastos);
    }
}
