package com.habitagest.finanzas;

/** El mes (AAAA-MM) es opcional: sin mes, la mora se aplica a todas las cuotas vencidas. */
public record AplicacionMora(String mes) {
}
