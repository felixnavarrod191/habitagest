package com.habitagest.pqr;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioPqr extends MongoRepository<Pqr, String> {

    List<Pqr> findByResidenteId(String residenteId);
}
