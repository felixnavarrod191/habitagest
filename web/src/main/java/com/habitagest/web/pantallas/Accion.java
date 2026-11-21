package com.habitagest.web.pantallas;

import java.util.Map;

/** Un botón de una fila de tabla: envía por POST los campos ocultos a la ruta indicada. */
public record Accion(String accion, String boton, Map<String, String> ocultos) {
}
