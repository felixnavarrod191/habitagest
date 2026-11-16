package com.habitagest.copropiedad;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioPropietarios extends MongoRepository<Propietario, String> {

    boolean existsByApartamentoId(String apartamentoId);
}
