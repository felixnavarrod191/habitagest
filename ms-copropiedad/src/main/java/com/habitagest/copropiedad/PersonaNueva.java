package com.habitagest.copropiedad;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo de PropietarioNuevo y de ResidenteNuevo: tienen los mismos campos. */
public record PersonaNueva(@NotBlank String nombre, @NotBlank String documento, @NotBlank String apartamentoId) {
}
