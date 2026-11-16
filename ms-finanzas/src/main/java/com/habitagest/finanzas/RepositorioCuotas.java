package com.habitagest.finanzas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioCuotas extends MongoRepository<Cuota, String> {

    List<Cuota> findByMes(String mes);

    List<Cuota> findByApartamentoId(String apartamentoId);

    List<Cuota> findByApartamentoIdAndMes(String apartamentoId, String mes);

    boolean existsByMes(String mes);
}
