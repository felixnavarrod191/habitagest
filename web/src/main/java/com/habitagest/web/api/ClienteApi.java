package com.habitagest.web.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Cliente de la web para el API Gateway ({@code GATEWAY_URL}, por defecto http://localhost:8090). Envía el usuario en
 * X-Usuario y el rol en X-Rol (el servicio de finanzas rechaza escrituras del revisor fiscal con ese encabezado).
 */
@Component
public class ClienteApi {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json;
    private final String base;

    public ClienteApi(ObjectMapper json, @Value("${gateway.url:http://localhost:8090}") String gateway) {
        this.json = json;
        this.base = gateway.replaceAll("/+$", "") + "/api";
    }

    /** Codifica un valor para usarlo en la parte de consulta de una ruta. */
    public static String codificar(String valor) {
        return URLEncoder.encode(valor == null ? "" : valor, StandardCharsets.UTF_8);
    }

    public JsonNode get(String ruta) {
        return enviar(HttpRequest.newBuilder(URI.create(base + ruta)).GET());
    }

    public JsonNode post(String ruta, Map<String, Object> cuerpo) {
        try {
            String texto = json.writeValueAsString(cuerpo == null ? Map.of() : cuerpo);
            return enviar(HttpRequest.newBuilder(URI.create(base + ruta))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(texto, StandardCharsets.UTF_8)));
        } catch (IOException error) {
            throw new ErrorDelServicio(400, "No se pudo preparar la solicitud.");
        }
    }

    private JsonNode enviar(HttpRequest.Builder peticion) {
        Authentication sesion = SecurityContextHolder.getContext().getAuthentication();
        if (sesion != null && sesion.getName() != null) {
            peticion.header("X-Usuario", codificar(sesion.getName()));
            for (GrantedAuthority autoridad : sesion.getAuthorities()) {
                peticion.header("X-Rol", autoridad.getAuthority().replaceFirst("^ROLE_", ""));
                break;
            }
        }
        try {
            HttpResponse<String> respuesta = http.send(peticion.timeout(Duration.ofSeconds(10)).build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            int estado = respuesta.statusCode();
            if (estado >= 200 && estado < 300) {
                return respuesta.body() == null || respuesta.body().isBlank() ? json.nullNode()
                        : json.readTree(respuesta.body());
            }
            throw new ErrorDelServicio(estado, mensajeDe(respuesta.body(), estado));
        } catch (IOException error) {
            throw new ErrorDelServicio(503, "No se pudo conectar con el servicio. Revisa que el gateway y los módulos estén encendidos.");
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
            throw new ErrorDelServicio(503, "La solicitud se interrumpió. Inténtalo de nuevo.");
        }
    }

    private String mensajeDe(String cuerpo, int estado) {
        try {
            JsonNode nodo = json.readTree(cuerpo == null ? "" : cuerpo);
            String mensaje = nodo.path("mensaje").asText("");
            if (!mensaje.isBlank()) {
                return mensaje;
            }
        } catch (IOException ignorado) {
            // el cuerpo no es JSON: se usa el mensaje por defecto
        }
        return "El servicio respondió con el error " + estado + ".";
    }
}
