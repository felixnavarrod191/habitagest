package com.habitagest.copropiedad;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioApartamentos extends MongoRepository<Apartamento, String> {

    List<Apartamento> findByTorre(String torre);

    boolean existsByTorreAndNumero(String torre, String numero);
}
