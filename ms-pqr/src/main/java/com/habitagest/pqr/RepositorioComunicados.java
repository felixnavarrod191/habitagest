package com.habitagest.pqr;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioComunicados extends MongoRepository<Comunicado, String> {
}
