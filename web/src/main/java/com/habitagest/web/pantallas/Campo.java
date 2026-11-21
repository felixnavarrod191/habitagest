package com.habitagest.web.pantallas;

import java.util.List;

/** Un campo de un formulario. Tipos: text, number, date, time, password, textarea, select y hidden. */
public record Campo(String nombre, String etiqueta, String tipo, String valor, List<Opcion> opciones) {
}
