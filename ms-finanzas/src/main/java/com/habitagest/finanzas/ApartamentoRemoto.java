package com.habitagest.finanzas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Apartamento tal como lo entrega ms-copropiedad; el coeficiente va en porcentaje. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApartamentoRemoto(String id, String torre, String numero, double coeficiente) {
}
