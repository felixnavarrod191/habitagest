package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * NFR-PH-007 (T123 y T124): el 100 % de los requisitos comprometidos tiene criterio de aceptación, tarea y prueba en
 * la matriz de trazabilidad. Un requisito sin prueba bloquea el release.
 */
class TrazabilidadTest {

    private static final Path RAIZ = Path.of("..");
    private static final Path SPEC = RAIZ.resolve("specs/001-habitagest/spec.md");
    private static final Path TAREAS = RAIZ.resolve("specs/001-habitagest/tasks.md");
    private static final Path MATRIZ = RAIZ.resolve("docs/trazabilidad.md");

    private static final Pattern REQUISITO = Pattern.compile("^\\| (REQ-PH-\\d{3}) \\|", Pattern.MULTILINE);
    private static final Pattern NO_FUNCIONAL = Pattern.compile("^\\| (NFR-PH-\\d{3}) \\|", Pattern.MULTILINE);
    private static final Pattern CRITERIO = Pattern.compile("AC-\\d{3}\\.\\d+");
    private static final Pattern TAREA = Pattern.compile("T\\d{3}");
    private static final Pattern NOMBRE_DE_PRUEBA = Pattern.compile("[A-Z][A-Za-z]+Test");

    private static String leer(Path archivo) throws IOException {
        return Files.readString(archivo, StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static Set<String> encontrar(Pattern patron, String texto) {
        Set<String> ids = new TreeSet<>();
        Matcher coincidencia = patron.matcher(texto);
        while (coincidencia.find()) {
            ids.add(coincidencia.group(1));
        }
        return ids;
    }

    private static Set<String> pruebasDelProyecto() throws IOException {
        Set<String> nombres = new TreeSet<>();
        try (Stream<Path> recorrido = Files.walk(RAIZ)) {
            recorrido.filter(Files::isRegularFile).forEach(archivo -> {
                String ruta = archivo.toString().replace('\\', '/');
                String nombre = archivo.getFileName().toString();
                if (ruta.contains("/src/test/java/") && nombre.endsWith("Test.java")) {
                    nombres.add(nombre.substring(0, nombre.length() - ".java".length()));
                }
            });
        }
        return nombres;
    }

    private static boolean hayRepositorio() {
        return Files.exists(SPEC) && Files.exists(TAREAS) && Files.exists(MATRIZ);
    }

    /** Las filas de la matriz de requisitos funcionales: id → celdas. */
    private static Map<String, List<String>> filasDeLaMatriz(String matriz) {
        Map<String, List<String>> filas = new LinkedHashMap<>();
        for (String linea : matriz.split("\n")) {
            if (linea.startsWith("| REQ-PH-")) {
                List<String> celdas = new ArrayList<>();
                for (String celda : linea.substring(1, linea.length() - 1).split("\\|")) {
                    celdas.add(celda.trim());
                }
                filas.put(celdas.get(0), celdas);
            }
        }
        return filas;
    }

    @Test
    void cadaRequisitoDeLaSpecTieneFilaEnLaMatriz() throws IOException {
        assumeTrue(hayRepositorio(), "No se encontró la raíz del repositorio.");
        Set<String> enLaSpec = encontrar(REQUISITO, leer(SPEC));
        Set<String> enLaMatriz = filasDeLaMatriz(leer(MATRIZ)).keySet();

        Set<String> sinFila = new TreeSet<>(enLaSpec);
        sinFila.removeAll(enLaMatriz);

        assertEquals(45, enLaSpec.size(), "La spec debe declarar los 45 requisitos funcionales.");
        assertTrue(sinFila.isEmpty(), "Requisitos sin fila en la matriz: " + sinFila);
    }

    @Test
    void cadaRequisitoTieneCriterioTareasYPruebaExistentes() throws IOException {
        assumeTrue(hayRepositorio(), "No se encontró la raíz del repositorio.");
        String spec = leer(SPEC);
        String tareas = leer(TAREAS);
        Set<String> pruebas = pruebasDelProyecto();
        List<String> problemas = new ArrayList<>();

        filasDeLaMatriz(leer(MATRIZ)).forEach((requisito, celdas) -> {
            if (celdas.size() < 7) {
                problemas.add(requisito + ": la fila no tiene las 7 columnas.");
                return;
            }
            Matcher criterios = CRITERIO.matcher(celdas.get(3));
            boolean hayCriterio = false;
            while (criterios.find()) {
                hayCriterio = true;
                if (!spec.contains("| " + criterios.group() + " |")) {
                    problemas.add(requisito + ": el criterio " + criterios.group() + " no está en la spec.");
                }
            }
            if (!hayCriterio) {
                problemas.add(requisito + ": sin criterio de aceptación.");
            }
            for (int columna : new int[] {4, 5}) {
                String tarea = celdas.get(columna);
                if (!TAREA.matcher(tarea).matches() || !tareas.contains("- [x] " + tarea + " ")) {
                    problemas.add(requisito + ": la tarea '" + tarea + "' no existe o no está marcada como hecha en tasks.md.");
                }
            }
            Matcher nombres = NOMBRE_DE_PRUEBA.matcher(celdas.get(6));
            boolean hayPrueba = false;
            while (nombres.find()) {
                hayPrueba = true;
                if (!pruebas.contains(nombres.group())) {
                    problemas.add(requisito + ": la prueba " + nombres.group() + " no existe en el repositorio.");
                }
            }
            if (!hayPrueba) {
                problemas.add(requisito + ": sin prueba (columna Prueba: '" + celdas.get(6) + "').");
            }
        });

        assertTrue(problemas.isEmpty(), "La matriz de trazabilidad tiene huecos: " + problemas);
    }

    @Test
    void cadaRequisitoNoFuncionalTieneUnaVerificacionConPruebaExistente() throws IOException {
        assumeTrue(hayRepositorio(), "No se encontró la raíz del repositorio.");
        String matriz = leer(MATRIZ);
        Set<String> declarados = encontrar(NO_FUNCIONAL, leer(SPEC));
        Set<String> pruebas = pruebasDelProyecto();
        List<String> problemas = new ArrayList<>();

        assertEquals(7, declarados.size(), "La spec debe declarar los 7 requisitos no funcionales.");
        for (String requisito : declarados) {
            String fila = null;
            for (String linea : matriz.split("\n")) {
                if (linea.startsWith("| " + requisito + " |")) {
                    fila = linea;
                }
            }
            if (fila == null) {
                problemas.add(requisito + ": sin fila en la matriz.");
                continue;
            }
            Matcher nombres = NOMBRE_DE_PRUEBA.matcher(fila);
            boolean hayPrueba = false;
            while (nombres.find()) {
                hayPrueba = true;
                if (!pruebas.contains(nombres.group())) {
                    problemas.add(requisito + ": la prueba " + nombres.group() + " no existe en el repositorio.");
                }
            }
            if (!hayPrueba) {
                problemas.add(requisito + ": su verificación no nombra ninguna prueba.");
            }
        }
        assertTrue(problemas.isEmpty(), "Requisitos no funcionales sin verificación: " + problemas);
    }

    @Test
    void todasLasTareasDeLaSpecEstanHechas() throws IOException {
        assumeTrue(hayRepositorio(), "No se encontró la raíz del repositorio.");
        List<String> pendientes = new ArrayList<>();
        for (String linea : leer(TAREAS).split("\n")) {
            if (linea.startsWith("- [ ] T")) {
                pendientes.add(linea.substring(6, 10));
            }
        }
        assertTrue(pendientes.isEmpty(), "Tareas sin hacer, que bloquean el release: " + pendientes);
    }
}
