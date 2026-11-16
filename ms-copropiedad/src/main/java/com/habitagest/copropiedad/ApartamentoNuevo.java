package com.habitagest.copropiedad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ApartamentoNuevo(@NotBlank String torre, @NotBlank String numero, @NotNull Double coeficiente) {
}
