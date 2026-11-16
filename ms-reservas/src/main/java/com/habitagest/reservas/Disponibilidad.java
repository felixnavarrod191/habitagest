package com.habitagest.reservas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** REQ-PH-019: divide el horario de una zona en tramos libres y ocupados para un día. */
final class Disponibilidad {

    private Disponibilidad() {
    }

    static List<Horario> calcular(LocalDate fecha, LocalTime inicioZona, LocalTime finZona, List<Reserva> confirmadas) {
        LocalDateTime apertura = fecha.atTime(inicioZona);
        LocalDateTime cierre = fecha.atTime(finZona);
        List<Reserva> delDia = new ArrayList<>();
        for (Reserva reserva : confirmadas) {
            if (reserva.getInicio().isBefore(cierre) && reserva.getFin().isAfter(apertura)) {
                delDia.add(reserva);
            }
        }
        delDia.sort(Comparator.comparing(Reserva::getInicio));
        List<Horario> tramos = new ArrayList<>();
        LocalDateTime cursor = apertura;
        for (Reserva reserva : delDia) {
            LocalDateTime desde = reserva.getInicio().isBefore(apertura) ? apertura : reserva.getInicio();
            LocalDateTime hasta = reserva.getFin().isAfter(cierre) ? cierre : reserva.getFin();
            if (desde.isAfter(cursor)) {
                tramos.add(new Horario(cursor, desde, Horario.LIBRE));
            }
            if (hasta.isAfter(cursor)) {
                LocalDateTime inicioOcupado = desde.isBefore(cursor) ? cursor : desde;
                tramos.add(new Horario(inicioOcupado, hasta, Horario.OCUPADO));
                cursor = hasta;
            }
        }
        if (cursor.isBefore(cierre)) {
            tramos.add(new Horario(cursor, cierre, Horario.LIBRE));
        }
        return tramos;
    }
}
