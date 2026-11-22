package com.habitagest.porteria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IngresoNuevo(@NotBlank String visitaId, @NotNull TipoIngreso tipo) {
}
