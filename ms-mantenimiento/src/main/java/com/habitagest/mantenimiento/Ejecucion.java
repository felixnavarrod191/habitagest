package com.habitagest.mantenimiento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record Ejecucion(@NotEmpty List<@Valid MaterialNuevo> materiales) {
}
