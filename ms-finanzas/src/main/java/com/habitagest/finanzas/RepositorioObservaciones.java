package com.habitagest.finanzas;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioObservaciones extends MongoRepository<Observacion, String> {
}
