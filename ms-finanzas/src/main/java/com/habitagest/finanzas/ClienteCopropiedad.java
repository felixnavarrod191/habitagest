package com.habitagest.finanzas;

import java.util.List;
import java.util.Optional;

/** Lo que ms-finanzas necesita de ms-copropiedad; la llamada es siempre por REST (nunca lee su base). */
public interface ClienteCopropiedad {

    /** Apartamentos del conjunto; con torre, solo los de esa torre. */
    List<ApartamentoRemoto> apartamentos(String torre);

    Optional<ApartamentoRemoto> apartamento(String id);
}
