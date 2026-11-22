package com.habitagest.pqr;

import jakarta.validation.constraints.NotBlank;

public record CambioEstado(@NotBlank String estado) {
}
