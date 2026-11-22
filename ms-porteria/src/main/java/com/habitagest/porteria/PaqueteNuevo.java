package com.habitagest.porteria;

import jakarta.validation.constraints.NotBlank;

public record PaqueteNuevo(@NotBlank String apartamentoId, @NotBlank String descripcion) {
}
