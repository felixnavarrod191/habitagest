package com.habitagest.web.pantallas;

import java.util.List;

/** Un formulario de la pantalla; el método es post (acción) o get (filtro). */
public record Formulario(String titulo, String metodo, String accion, String boton, List<Campo> campos) {
}
