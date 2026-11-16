package com.habitagest.gateway;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

/** T028: el gateway tiene una ruta por cada uno de los 6 servicios. */
@SpringBootTest
class RutasGatewayTest {

    @Autowired
    private RouteLocator rutas;

    @Test
    void elGatewayEnrutaLosSeisServicios() {
        Map<String, Route> porId = rutas.getRoutes().collectMap(Route::getId).block();

        assertNotNull(porId);
        assertEquals(Set.of("copropiedad", "finanzas", "reservas", "pqr", "porteria", "mantenimiento"), porId.keySet());
    }

    @Test
    void cadaRutaTieneSuDestino() {
        Map<String, Route> porId = rutas.getRoutes().collectMap(Route::getId).block();

        assertNotNull(porId);
        porId.values().forEach(ruta -> assertNotNull(ruta.getUri()));
    }
}
