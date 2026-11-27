package com.habitagest.pqr;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.Reader;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.Yaml;

/**
 * NFR-PH-006 (T122): el contrato OpenAPI de ms-pqr coincide con lo que el servicio expone. Cada operación del
 * contrato (método y ruta) existe en un controlador y cada endpoint de un controlador está en el contrato.
 */
class ContratoOpenApiTest {

    private static final Path CONTRATO = Path.of("..", "specs", "001-habitagest", "contracts", "pqr.yaml");
    private static final Set<String> METODOS = Set.of("get", "post", "put", "delete", "patch");

    @Test
    @SuppressWarnings("unchecked")
    void lasOperacionesDelContratoSonLasDelServicio() throws Exception {
        assumeTrue(Files.exists(CONTRATO), "No se encontró el contrato " + CONTRATO.toAbsolutePath().normalize());

        Set<String> delContrato = new TreeSet<>();
        try (Reader lector = Files.newBufferedReader(CONTRATO)) {
            Map<String, Object> contrato = new Yaml().load(lector);
            Map<String, Object> rutas = (Map<String, Object>) contrato.get("paths");
            rutas.forEach((ruta, operaciones) -> ((Map<String, Object>) operaciones).keySet().stream()
                    .filter(METODOS::contains)
                    .forEach(metodo -> delContrato.add(metodo.toUpperCase() + " " + ruta)));
        }

        Set<String> delCodigo = operacionesDelCodigo();

        Set<String> faltanEnElServicio = new TreeSet<>(delContrato);
        faltanEnElServicio.removeAll(delCodigo);
        Set<String> faltanEnElContrato = new TreeSet<>(delCodigo);
        faltanEnElContrato.removeAll(delContrato);

        assertTrue(faltanEnElServicio.isEmpty(), "El contrato las declara y el servicio no las tiene: " + faltanEnElServicio);
        assertTrue(faltanEnElContrato.isEmpty(), "El servicio las tiene y el contrato no las declara: " + faltanEnElContrato);
        assertTrue(!delContrato.isEmpty(), "El contrato no declara ninguna operación.");
    }

    private static Set<String> operacionesDelCodigo() throws Exception {
        Set<String> operaciones = new TreeSet<>();
        ClassPathScanningCandidateComponentProvider buscador = new ClassPathScanningCandidateComponentProvider(false);
        buscador.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        for (BeanDefinition definicion : buscador.findCandidateComponents("com.habitagest.pqr")) {
            Class<?> controlador = Class.forName(definicion.getBeanClassName());
            RequestMapping base = AnnotatedElementUtils.findMergedAnnotation(controlador, RequestMapping.class);
            String prefijo = base == null || base.path().length == 0 ? "" : base.path()[0];
            for (Method metodo : controlador.getDeclaredMethods()) {
                RequestMapping mapeo = AnnotatedElementUtils.findMergedAnnotation(metodo, RequestMapping.class);
                if (mapeo == null) {
                    continue;
                }
                String[] rutas = mapeo.path().length == 0 ? new String[] {""} : mapeo.path();
                for (RequestMethod verbo : mapeo.method()) {
                    for (String ruta : rutas) {
                        operaciones.add(verbo.name() + " " + prefijo + ruta);
                    }
                }
            }
        }
        return operaciones;
    }
}
