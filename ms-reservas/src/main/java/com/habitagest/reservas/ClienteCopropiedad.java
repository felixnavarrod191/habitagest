package com.habitagest.reservas;

/** Lo que ms-reservas necesita de ms-copropiedad: validar que el apartamento existe (REQ-PH-020). */
public interface ClienteCopropiedad {

    boolean existeApartamento(String apartamentoId);
}
