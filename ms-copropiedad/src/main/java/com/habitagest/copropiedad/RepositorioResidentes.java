package com.habitagest.copropiedad;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioResidentes extends MongoRepository<Residente, String> {

    List<Residente> findByApartamentoId(String apartamentoId);
}
