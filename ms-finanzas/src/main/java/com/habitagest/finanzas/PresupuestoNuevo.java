package com.habitagest.finanzas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresupuestoNuevo(@NotBlank String mes, @NotNull Double valor) {
}
