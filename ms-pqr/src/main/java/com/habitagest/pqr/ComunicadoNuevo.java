package com.habitagest.pqr;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ComunicadoNuevo(@NotBlank String titulo, @NotBlank String texto, @NotNull LocalDate vigenteHasta) {
}
