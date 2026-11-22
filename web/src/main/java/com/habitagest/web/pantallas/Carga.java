package com.habitagest.web.pantallas;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee datos de los servicios para armar una pantalla. Si un servicio falla, la pantalla igual se muestra, vacía, y el
 * mensaje de error queda en {@link #avisos()}.
 */
public class Carga {

    private final ClienteApi api;
    private final List<String> avisos = new ArrayList<>();

    public Carga(ClienteApi api) {
        this.api = api;
    }

    public List<JsonNode> lista(String ruta) {
        List<JsonNode> elementos = new ArrayList<>();
        try {
            JsonNode nodo = api.get(ruta);
            if (nodo != null && nodo.isArray()) {
                nodo.forEach(elementos::add);
            }
        } catch (ErrorDelServicio error) {
            avisos.add(error.getMessage());
        }
        return elementos;
    }

    /** El objeto de la ruta, o null si el servicio falló (el aviso queda registrado). */
    public JsonNode objeto(String ruta) {
        try {
            return api.get(ruta);
        } catch (ErrorDelServicio error) {
            avisos.add(error.getMessage());
            return null;
        }
    }

    public List<String> avisos() {
        return avisos;
    }
}
