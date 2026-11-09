package com.habitagest.web;

import java.util.Map;

/** Valida al arrancar que existan las variables de entorno obligatorias (NFR-PH-005). */
public final class ConfiguracionObligatoria {

    private ConfiguracionObligatoria() {
    }

    public static void exigir(Map<String, String> entorno, String... nombres) {
        for (String nombre : nombres) {
            String valor = entorno.get(nombre);
            if (valor == null || valor.isBlank()) {
                throw new IllegalStateException(
                        "Falta la variable de entorno " + nombre + ". Defínela antes de arrancar el servicio.");
            }
        }
    }
}
