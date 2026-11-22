package com.habitagest.porteria;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioPaquetes extends MongoRepository<Paquete, String> {

    List<Paquete> findByApartamentoId(String apartamentoId);
}
