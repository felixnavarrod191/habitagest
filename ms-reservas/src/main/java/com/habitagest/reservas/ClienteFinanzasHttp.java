package com.habitagest.reservas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ClienteFinanzasHttp implements ClienteFinanzas {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EstadoMoraRemoto(boolean enMora) {
    }

    private final RestClient cliente;

    public ClienteFinanzasHttp(@Value("${finanzas.url}") String url) {
        this.cliente = RestClient.builder().baseUrl(url).build();
    }

    @Override
    public boolean enMora(String apartamentoId) {
        try {
            EstadoMoraRemoto estado = cliente.get().uri("/apartamentos/{id}/mora", apartamentoId).retrieve()
                    .body(EstadoMoraRemoto.class);
            return estado != null && estado.enMora();
        } catch (HttpClientErrorException.NotFound sinCartera) {
            return false;
        } catch (RestClientException error) {
            throw ErrorDeNegocio.servicioNoDisponible("No se pudo consultar ms-finanzas.");
        }
    }
}
