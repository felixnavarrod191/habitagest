package com.habitagest.finanzas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo de POST /observaciones: sobre qué es la observación (un pago o una orden) y qué se observa. */
public record ObservacionNueva(@NotBlank @Size(max = 120) String referencia, @NotBlank @Size(max = 1000) String texto) {
}
