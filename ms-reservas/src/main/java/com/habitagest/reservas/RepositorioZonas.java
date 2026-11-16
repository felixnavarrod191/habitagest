package com.habitagest.reservas;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioZonas extends MongoRepository<Zona, String> {

    boolean existsByNombre(String nombre);
}
