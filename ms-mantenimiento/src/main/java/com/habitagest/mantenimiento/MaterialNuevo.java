package com.habitagest.mantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MaterialNuevo(@NotBlank String descripcion, @NotNull @Positive Long valor) {
}
