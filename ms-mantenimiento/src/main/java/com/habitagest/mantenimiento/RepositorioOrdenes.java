package com.habitagest.mantenimiento;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioOrdenes extends MongoRepository<Orden, String> {

    List<Orden> findByEstado(String estado);

    List<Orden> findByProveedorId(String proveedorId);
}
