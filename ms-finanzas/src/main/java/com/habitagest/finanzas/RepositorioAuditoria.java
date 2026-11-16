package com.habitagest.finanzas;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RepositorioAuditoria extends MongoRepository<RegistroAuditoria, String> {
}
