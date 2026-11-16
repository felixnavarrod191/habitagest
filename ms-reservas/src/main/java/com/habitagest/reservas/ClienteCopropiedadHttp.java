package com.habitagest.reservas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ClienteCopropiedadHttp implements ClienteCopropiedad {

    private final RestClient cliente;

    public ClienteCopropiedadHttp(@Value("${copropiedad.url}") String url) {
        this.cliente = RestClient.builder().baseUrl(url).build();
    }

    @Override
    public boolean existeApartamento(String apartamentoId) {
        try {
            cliente.get().uri("/apartamentos/{id}", apartamentoId).retrieve().toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound noExiste) {
            return false;
        } catch (RestClientException error) {
            throw ErrorDeNegocio.servicioNoDisponible("No se pudo consultar ms-copropiedad.");
        }
    }
}
