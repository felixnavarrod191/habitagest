package com.habitagest.finanzas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/** RN-01: cuota = presupuesto mensual por el coeficiente de copropiedad (en porcentaje). */
final class CalculoCuotas {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private CalculoCuotas() {
    }

    /**
     * Calcula la cuota de cada apartamento con dos decimales. Si los coeficientes suman exactamente 100 %,
     * la diferencia por redondeo se suma a la cuota del coeficiente mayor para que el total sea el presupuesto.
     */
    static List<BigDecimal> repartir(BigDecimal presupuesto, List<BigDecimal> coeficientes) {
        List<BigDecimal> cuotas = new ArrayList<>();
        BigDecimal sumaCoeficientes = BigDecimal.ZERO;
        BigDecimal sumaCuotas = BigDecimal.ZERO;
        int mayor = 0;
        for (int i = 0; i < coeficientes.size(); i++) {
            BigDecimal coeficiente = coeficientes.get(i);
            BigDecimal cuota = presupuesto.multiply(coeficiente).divide(CIEN, 2, RoundingMode.HALF_UP);
            cuotas.add(cuota);
            sumaCoeficientes = sumaCoeficientes.add(coeficiente);
            sumaCuotas = sumaCuotas.add(cuota);
            if (coeficiente.compareTo(coeficientes.get(mayor)) > 0) {
                mayor = i;
            }
        }
        if (!cuotas.isEmpty() && sumaCoeficientes.compareTo(CIEN) == 0) {
            BigDecimal diferencia = presupuesto.subtract(sumaCuotas);
            if (diferencia.signum() != 0) {
                cuotas.set(mayor, cuotas.get(mayor).add(diferencia));
            }
        }
        return cuotas;
    }
}
