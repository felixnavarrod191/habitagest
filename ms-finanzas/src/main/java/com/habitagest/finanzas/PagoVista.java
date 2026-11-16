package com.habitagest.finanzas;

import java.time.LocalDateTime;

public record PagoVista(String id, String cuotaId, double valor, LocalDateTime fecha) {
}
