package com.habitagest.reservas;

import java.time.LocalDateTime;

/** Tramo de una zona en un día: LIBRE u OCUPADO (schema Horario). */
public record Horario(LocalDateTime inicio, LocalDateTime fin, String estado) {

    public static final String LIBRE = "LIBRE";
    public static final String OCUPADO = "OCUPADO";
}
