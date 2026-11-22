package com.habitagest.pqr;

import jakarta.validation.constraints.NotBlank;

public record PqrNueva(@NotBlank String residenteId, @NotBlank String asunto, @NotBlank String descripcion) {
}
