package com.habitagest.reservas;

import jakarta.validation.constraints.NotBlank;

public record ZonaNueva(@NotBlank String nombre, @NotBlank String horaInicio, @NotBlank String horaFin) {
}
