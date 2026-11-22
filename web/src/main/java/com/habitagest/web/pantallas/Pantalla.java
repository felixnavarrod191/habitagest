package com.habitagest.web.pantallas;

import java.util.List;

/** Todo lo que necesita la plantilla pantalla.html: menú del área, avisos, formularios y tablas. */
public record Pantalla(String titulo, String descripcion, String area, List<Enlace> menu, String actual,
        List<String> avisos, List<Formulario> formularios, List<Tabla> tablas) {
}
