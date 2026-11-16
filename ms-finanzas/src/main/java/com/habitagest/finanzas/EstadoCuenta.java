package com.habitagest.finanzas;

import java.util.List;

public record EstadoCuenta(String apartamentoId, List<CuotaVista> cuotas, List<PagoVista> pagos, double saldo,
        double mora) {
}
