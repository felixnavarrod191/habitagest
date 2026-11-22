package com.habitagest.porteria;

import jakarta.validation.constraints.NotBlank;

public record NovedadNueva(@NotBlank String texto) {
}
