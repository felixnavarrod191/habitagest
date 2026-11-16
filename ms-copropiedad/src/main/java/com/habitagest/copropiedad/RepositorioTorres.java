package com.habitagest.copropiedad;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioTorres extends MongoRepository<Torre, String> {

    boolean existsByNombre(String nombre);
}
