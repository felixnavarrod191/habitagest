package com.habitagest.mantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrdenNueva(@NotBlank String descripcion, @NotBlank String zona, @NotNull @Positive Long costoEstimado,
        String proveedorId) {
}
