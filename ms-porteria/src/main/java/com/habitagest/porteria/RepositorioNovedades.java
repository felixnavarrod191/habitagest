package com.habitagest.porteria;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioNovedades extends MongoRepository<Novedad, String> {
}
