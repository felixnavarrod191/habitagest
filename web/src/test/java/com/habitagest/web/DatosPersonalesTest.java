package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

/**
 * REQ-PH-045 y NFR-PH-004 (AC-045.1, T111 y T112): ningún dato personal real aparece en el repositorio. Revisa los
 * archivos de texto de todo el proyecto: solo se admiten correos y teléfonos ficticios. La única excepción es el
 * contacto del equipo, que se publica solo en la página de aterrizaje por decisión de los tres integrantes.
 */
class DatosPersonalesTest {

    private static final Path RAIZ = Path.of("..");
    private static final Set<String> EXTENSIONES = Set.of("java", "html", "md", "yaml", "yml", "properties", "ps1",
            "txt", "xml", "json", "css", "js");
    private static final Set<String> CARPETAS_IGNORADAS = Set.of(".git", "target", ".settings", "node_modules");

    /** Contacto del equipo de desarrollo (docs/tratamiento-de-datos.md, sección 4). */
    private static final Set<String> CONTACTO_DEL_EQUIPO = Set.of("felixnavarro111o@gmail.com",
            "pandalcoholic@gmail.com", "estivenalexanderr7@gmail.com");
    private static final Set<String> ARCHIVOS_DEL_CONTACTO = Set.of("aterrizaje.html", "AterrizajeTest.java",
            "DatosPersonalesTest.java");

    private static final Pattern CORREO = Pattern.compile("[A-Za-z0-9._%+-]+@([A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,})");
    private static final Pattern TELEFONO_MOVIL = Pattern.compile("(?<!\\d)3\\d{2}[ .-]?\\d{3}[ .-]?\\d{4}(?!\\d)");

    private static boolean esFicticio(String correo, String dominio) {
        String d = dominio.toLowerCase(Locale.ROOT);
        return d.equals("example.com") || d.equals("example.org") || d.equals("example.net") || d.endsWith(".test")
                || d.endsWith(".local") || d.endsWith(".invalid") || correo.toLowerCase(Locale.ROOT).startsWith("demo_");
    }

    private static List<Path> archivosDeTexto() throws IOException {
        try (Stream<Path> recorrido = Files.walk(RAIZ)) {
            return recorrido.filter(Files::isRegularFile).filter(archivo -> {
                for (Path parte : RAIZ.relativize(archivo)) {
                    if (CARPETAS_IGNORADAS.contains(parte.toString())) {
                        return false;
                    }
                }
                String nombre = archivo.getFileName().toString();
                int punto = nombre.lastIndexOf('.');
                return punto >= 0 && EXTENSIONES.contains(nombre.substring(punto + 1).toLowerCase(Locale.ROOT));
            }).toList();
        }
    }

    private static String leer(Path archivo) {
        try {
            return Files.readString(archivo, StandardCharsets.UTF_8);
        } catch (IOException error) {
            return "";
        }
    }

    /** AC-045.1: los datos y las pruebas del proyecto se revisan antes de cada entrega y no hay datos personales reales. */
    @Test
    void elRepositorioNoTieneCorreosNiTelefonosReales() throws IOException {
        assumeTrue(Files.isDirectory(RAIZ.resolve("specs")), "No se encontró la raíz del repositorio.");
        Set<String> hallazgos = new TreeSet<>();
        for (Path archivo : archivosDeTexto()) {
            String texto = leer(archivo);
            Matcher correos = CORREO.matcher(texto);
            while (correos.find()) {
                String correo = correos.group();
                if (!esFicticio(correo, correos.group(1)) && !CONTACTO_DEL_EQUIPO.contains(correo.toLowerCase(Locale.ROOT))) {
                    hallazgos.add(RAIZ.relativize(archivo) + " → correo " + correo);
                }
            }
            Matcher telefonos = TELEFONO_MOVIL.matcher(texto);
            while (telefonos.find()) {
                hallazgos.add(RAIZ.relativize(archivo) + " → teléfono " + telefonos.group());
            }
        }
        assertTrue(hallazgos.isEmpty(), "Posibles datos personales reales: " + hallazgos);
    }

    @Test
    void elContactoDelEquipoSoloApareceEnLaPaginaDeAterrizaje() throws IOException {
        assumeTrue(Files.isDirectory(RAIZ.resolve("specs")), "No se encontró la raíz del repositorio.");
        Set<String> fuera = new TreeSet<>();
        for (Path archivo : archivosDeTexto()) {
            String texto = leer(archivo).toLowerCase(Locale.ROOT);
            boolean tieneContacto = CONTACTO_DEL_EQUIPO.stream().anyMatch(texto::contains);
            if (tieneContacto && !ARCHIVOS_DEL_CONTACTO.contains(archivo.getFileName().toString())) {
                fuera.add(RAIZ.relativize(archivo).toString());
            }
        }
        assertTrue(fuera.isEmpty(), "El contacto del equipo solo va en la página de aterrizaje y en su prueba: " + fuera);
    }
}
