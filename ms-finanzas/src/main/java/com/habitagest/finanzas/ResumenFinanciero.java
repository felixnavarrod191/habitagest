package com.habitagest.finanzas;

/** REQ-PH-043: el total de cartera (saldo pendiente de las cuotas del mes) y los gastos de mantenimiento del mes. */
public record ResumenFinanciero(String mes, double totalCartera, double gastosMantenimiento) {
}
