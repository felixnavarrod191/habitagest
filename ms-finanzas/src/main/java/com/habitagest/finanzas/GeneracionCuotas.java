package com.habitagest.finanzas;

import jakarta.validation.constraints.NotBlank;

public record GeneracionCuotas(@NotBlank String mes) {
}
