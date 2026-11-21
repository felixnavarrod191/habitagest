package com.habitagest.web.pantallas;

import java.util.List;

public record Tabla(String titulo, List<String> columnas, List<Fila> filas, String vacio) {
}
