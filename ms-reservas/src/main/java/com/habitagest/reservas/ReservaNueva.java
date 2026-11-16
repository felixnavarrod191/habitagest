package com.habitagest.reservas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservaNueva(@NotBlank String zonaId, @NotBlank String apartamentoId, @NotNull LocalDateTime inicio,
        @NotNull LocalDateTime fin) {
}
