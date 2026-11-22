package com.habitagest.porteria;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioVisitas extends MongoRepository<Visita, String> {

    List<Visita> findByFecha(LocalDate fecha);
}
