package com.habitagest.finanzas;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Da formato de pesos colombianos a los mensajes: 150.000 o 150.000,5. */
final class Pesos {

    private Pesos() {
    }

    static String formato(BigDecimal valor) {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(Locale.forLanguageTag("es-CO"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.##", simbolos).format(valor);
    }
}
