package com.habitagest.porteria;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioIngresos extends MongoRepository<Ingreso, String> {

    List<Ingreso> findByVisitaId(String visitaId);
}
