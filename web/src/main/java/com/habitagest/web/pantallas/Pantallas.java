package com.habitagest.web.pantallas;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ErrorDelServicio;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Constructores cortos de las piezas de una pantalla y utilidades para mostrar los datos de los servicios. */
public final class Pantallas {

    private Pantallas() {
    }

    // ----- campos -----
    public static Campo texto(String nombre, String etiqueta) {
        return new Campo(nombre, etiqueta, "text", "", List.of());
    }

    public static Campo area(String nombre, String etiqueta) {
        return new Campo(nombre, etiqueta, "textarea", "", List.of());
    }

    public static Campo numero(String nombre, String etiqueta) {
        return new Campo(nombre, etiqueta, "number", "", List.of());
    }

    public static Campo fecha(String nombre, String etiqueta, String valor) {
        return new Campo(nombre, etiqueta, "date", valor, List.of());
    }

    /** Selector de mes con calendario (AAAA-MM). Sin valor, propone el mes actual. */
    public static Campo mes(String nombre, String etiqueta) {
        String actual = java.time.YearMonth.now(java.time.ZoneId.of("America/Bogota")).toString();
        return new Campo(nombre, etiqueta, "month", actual, List.of());
    }

    public static Campo hora(String nombre, String etiqueta, String valor) {
        return new Campo(nombre, etiqueta, "time", valor, List.of());
    }

    public static Campo seleccion(String nombre, String etiqueta, List<Opcion> opciones) {
        return new Campo(nombre, etiqueta, "select", "", opciones);
    }

    public static Campo oculto(String nombre, String valor) {
        return new Campo(nombre, "", "hidden", valor, List.of());
    }

    // ----- formularios, tablas y filas -----
    public static Formulario post(String titulo, String accion, String boton, Campo... campos) {
        return new Formulario(titulo, "post", accion, boton, Arrays.asList(campos));
    }

    public static Formulario consulta(String titulo, String accion, String boton, Campo... campos) {
        return new Formulario(titulo, "get", accion, boton, Arrays.asList(campos));
    }

    public static Tabla tabla(String titulo, String vacio, List<String> columnas, List<Fila> filas) {
        return new Tabla(titulo, columnas, filas, vacio);
    }

    public static Fila fila(List<String> celdas, Accion... acciones) {
        return new Fila(celdas, Arrays.asList(acciones));
    }

    /** Un botón de fila: los pares son nombre y valor de los campos ocultos que se envían. */
    public static Accion accion(String ruta, String boton, String... pares) {
        Map<String, String> ocultos = new LinkedHashMap<>();
        for (int i = 0; i + 1 < pares.length; i += 2) {
            ocultos.put(pares[i], pares[i + 1]);
        }
        return new Accion(ruta, boton, ocultos);
    }

    public static List<String> columnas(String... nombres) {
        return Arrays.asList(nombres);
    }

    public static List<String> celdas(String... valores) {
        return Arrays.asList(valores);
    }

    // ----- lectura de datos -----
    public static String t(JsonNode nodo, String campo) {
        return nodo == null ? "" : nodo.path(campo).asText("");
    }

    public static double d(JsonNode nodo, String campo) {
        return nodo == null ? 0 : nodo.path(campo).asDouble(0);
    }

    public static boolean b(JsonNode nodo, String campo) {
        return nodo != null && nodo.path(campo).asBoolean(false);
    }

    /** Pesos colombianos con punto de miles, por ejemplo $1.500.000. */
    public static String pesos(double valor) {
        return "$" + String.format("%,.0f", valor).replace(',', '.');
    }

    /** "2026-11-19T10:00:00" se muestra como "2026-11-19 10:00". */
    public static String fechaHora(String iso) {
        if (iso == null || iso.isBlank()) {
            return "";
        }
        String limpio = iso.replace('T', ' ');
        return limpio.length() > 16 ? limpio.substring(0, 16) : limpio;
    }

    /** Convierte un texto escrito por el usuario (acepta coma decimal) en número. */
    public static double numero(String texto) {
        try {
            return Double.parseDouble(texto == null ? "" : texto.trim().replace(',', '.'));
        } catch (NumberFormatException error) {
            throw new ErrorDelServicio(400, "Escribe un número válido.");
        }
    }

    public static long entero(String texto) {
        return Math.round(numero(texto));
    }

    public static String limpio(String texto) {
        return texto == null ? "" : texto.trim();
    }

    /** "Torre 1": no repite la palabra si el nombre ya empieza por "Torre" ("Torre 2" no pasa a "Torre Torre 2"). */
    public static String nombreTorre(String nombre) {
        String limpio = limpio(nombre);
        return limpio.toLowerCase(java.util.Locale.ROOT).startsWith("torre") ? limpio : "Torre " + limpio;
    }

    /** "Torre 1 · Apto 301" para listas de apartamentos. */
    public static String nombreApartamento(JsonNode apartamento) {
        return nombreTorre(t(apartamento, "torre")) + " · Apto " + t(apartamento, "numero");
    }

    public static List<Opcion> opcionesDeApartamentos(List<JsonNode> apartamentos) {
        List<Opcion> opciones = new ArrayList<>();
        for (JsonNode apartamento : apartamentos) {
            opciones.add(new Opcion(t(apartamento, "id"), nombreApartamento(apartamento)));
        }
        return opciones;
    }

    public static List<Opcion> opciones(String... paresValorTexto) {
        List<Opcion> opciones = new ArrayList<>();
        for (int i = 0; i + 1 < paresValorTexto.length; i += 2) {
            opciones.add(new Opcion(paresValorTexto[i], paresValorTexto[i + 1]));
        }
        return opciones;
    }
}
