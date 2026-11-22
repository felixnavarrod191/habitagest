package com.habitagest.porteria;

/** Lo que ms-porteria necesita de ms-copropiedad: validar que el apartamento existe (REQ-PH-033). */
public interface ClienteCopropiedad {

    boolean existeApartamento(String apartamentoId);
}
