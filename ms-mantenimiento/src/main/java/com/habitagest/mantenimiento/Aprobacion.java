package com.habitagest.mantenimiento;

import jakarta.validation.constraints.NotNull;

public record Aprobacion(@NotNull AprobadoPor aprobadoPor) {
}
