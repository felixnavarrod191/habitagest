package com.habitagest.reservas;

/** Lo que ms-reservas necesita de ms-finanzas: saber si un apartamento está en mora (REQ-PH-022). */
public interface ClienteFinanzas {

    boolean enMora(String apartamentoId);
}
