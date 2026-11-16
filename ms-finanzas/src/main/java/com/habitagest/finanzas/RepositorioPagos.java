package com.habitagest.finanzas;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioPagos extends MongoRepository<Pago, String> {

    List<Pago> findByApartamentoId(String apartamentoId);
}
