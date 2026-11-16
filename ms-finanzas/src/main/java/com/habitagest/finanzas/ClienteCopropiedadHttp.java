package com.habitagest.finanzas;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
    public List<ApartamentoRemoto> apartamentos(String torre) {
        try {
            List<ApartamentoRemoto> lista = (torre == null || torre.isBlank())
                    ? cliente.get().uri("/apartamentos").retrieve()
                            .body(new ParameterizedTypeReference<List<ApartamentoRemoto>>() {
                            })
                    : cliente.get().uri("/apartamentos?torre={torre}", torre).retrieve()
                            .body(new ParameterizedTypeReference<List<ApartamentoRemoto>>() {
                            });
            return lista == null ? List.of() : lista;
        } catch (RestClientException error) {
            throw ErrorDeNegocio.servicioNoDisponible("No se pudo consultar ms-copropiedad.");
        }
    }

    @Override
    public Optional<ApartamentoRemoto> apartamento(String id) {
        try {
            return Optional.ofNullable(
                    cliente.get().uri("/apartamentos/{id}", id).retrieve().body(ApartamentoRemoto.class));
        } catch (HttpClientErrorException.NotFound noExiste) {
            return Optional.empty();
        } catch (RestClientException error) {
            throw ErrorDeNegocio.servicioNoDisponible("No se pudo consultar ms-copropiedad.");
        }
    }
}
