package com.habitagest.finanzas;

/** Lo que ms-finanzas necesita de ms-mantenimiento; la llamada es siempre por REST (nunca lee su base). */
public interface ClienteMantenimiento {

    /** Total de gastos de mantenimiento (órdenes completadas) de un mes con formato AAAA-MM, en pesos. */
    double gastosDelMes(String mes);
}
