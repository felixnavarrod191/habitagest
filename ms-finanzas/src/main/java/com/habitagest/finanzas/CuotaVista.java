package com.habitagest.finanzas;

import java.time.LocalDate;

/** La cuota tal como la ve el contrato (schema Cuota): el estado se calcula con la fecha de hoy. */
public record CuotaVista(String id, String apartamentoId, String mes, double valor, double saldo, double mora,
        String estado) {

    static CuotaVista de(Cuota cuota, LocalDate hoy) {
        return new CuotaVista(cuota.getId(), cuota.getApartamentoId(), cuota.getMes(), cuota.getValor(),
                cuota.getSaldo(), cuota.getMora(), estadoDe(cuota, hoy));
    }

    static String estadoDe(Cuota cuota, LocalDate hoy) {
        if (cuota.getSaldo() <= 0) {
            return "PAGADA";
        }
        return hoy.isAfter(cuota.getVencimiento()) ? "VENCIDA" : "PENDIENTE";
    }
}
