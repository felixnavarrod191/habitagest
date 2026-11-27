package com.habitagest.finanzas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ClienteMantenimientoHttp implements ClienteMantenimiento {

    /** Respuesta de GET /gastos de ms-mantenimiento. */
    public record GastosRemotos(String mes, double total) {
    }

    private final RestClient cliente;

    public ClienteMantenimientoHttp(@Value("${mantenimiento.url}") String url) {
        this.cliente = RestClient.builder().baseUrl(url).build();
    }

    @Override
    public double gastosDelMes(String mes) {
        try {
            GastosRemotos gastos = cliente.get().uri("/gastos?mes={mes}", mes).retrieve().body(GastosRemotos.class);
            return gastos == null ? 0 : gastos.total();
        } catch (RestClientException error) {
            throw ErrorDeNegocio.servicioNoDisponible("No se pudo consultar ms-mantenimiento.");
        }
    }
}
