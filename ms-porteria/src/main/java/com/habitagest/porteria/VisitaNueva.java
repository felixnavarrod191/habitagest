package com.habitagest.porteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record VisitaNueva(@NotBlank String apartamentoId, @NotBlank String nombre, @NotBlank String documento,
        @NotNull LocalDate fecha) {
}
