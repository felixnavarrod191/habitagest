package com.habitagest.copropiedad;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

/** T027: los errores salen con el cuerpo {codigo, mensaje} y el estado HTTP del contrato. */
class ManejadorErroresTest {

    private final ManejadorErrores manejador = new ManejadorErrores();

    @Test
    void unaReglaDeNegocioSaleConEstado409YSuCodigo() {
        ResponseEntity<ErrorApi> respuesta = manejador.negocio(ErrorDeNegocio.reglaDeNegocio("No se puede."));
        assertEquals(409, respuesta.getStatusCode().value());
        assertEquals("E409", respuesta.getBody().codigo());
        assertEquals("No se puede.", respuesta.getBody().mensaje());
    }

    @Test
    void loQueNoExisteSaleConEstado404() {
        ResponseEntity<ErrorApi> respuesta = manejador.negocio(ErrorDeNegocio.noEncontrado("No existe."));
        assertEquals(404, respuesta.getStatusCode().value());
        assertEquals("E404", respuesta.getBody().codigo());
    }

    @Test
    void unaSolicitudInvalidaSaleConEstado400() {
        ResponseEntity<ErrorApi> respuesta = manejador.negocio(ErrorDeNegocio.solicitudInvalida("Dato inválido."));
        assertEquals(400, respuesta.getStatusCode().value());
        assertEquals("E400", respuesta.getBody().codigo());
    }

    @Test
    void sinPermisoSaleConEstado403() {
        ResponseEntity<ErrorApi> respuesta = manejador.negocio(ErrorDeNegocio.sinPermiso("Sin permiso."));
        assertEquals(403, respuesta.getStatusCode().value());
        assertEquals("E403", respuesta.getBody().codigo());
    }

    @Test
    void unServicioCaidoSaleConEstado503() {
        ResponseEntity<ErrorApi> respuesta = manejador.negocio(ErrorDeNegocio.servicioNoDisponible("Servicio caído."));
        assertEquals(503, respuesta.getStatusCode().value());
        assertEquals("E503", respuesta.getBody().codigo());
    }
}
