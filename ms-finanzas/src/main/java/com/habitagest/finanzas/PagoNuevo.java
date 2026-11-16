package com.habitagest.finanzas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PagoNuevo(@NotBlank String cuotaId, @NotNull Double valor) {
}
