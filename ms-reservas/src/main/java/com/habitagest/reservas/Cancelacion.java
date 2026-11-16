package com.habitagest.reservas;

import jakarta.validation.constraints.NotNull;

public record Cancelacion(@NotNull Solicitante solicitante) {
}
