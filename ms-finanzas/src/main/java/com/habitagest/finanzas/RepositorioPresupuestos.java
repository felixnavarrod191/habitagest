package com.habitagest.finanzas;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioPresupuestos extends MongoRepository<Presupuesto, String> {

    Optional<Presupuesto> findByMes(String mes);

    boolean existsByMes(String mes);
}
