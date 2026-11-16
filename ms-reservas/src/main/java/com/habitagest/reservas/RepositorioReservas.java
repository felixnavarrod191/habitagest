package com.habitagest.reservas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioReservas extends MongoRepository<Reserva, String> {

    List<Reserva> findByZonaIdAndEstado(String zonaId, String estado);

    List<Reserva> findByApartamentoIdAndEstado(String apartamentoId, String estado);
}
