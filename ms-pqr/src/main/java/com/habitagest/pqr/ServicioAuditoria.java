package com.habitagest.pqr;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/** T026: componente común de auditoría de cambios de estado (usuario y fecha-hora). */
@Service
public class ServicioAuditoria {

    private final RepositorioAuditoria repositorio;
    private final Clock reloj;

    public ServicioAuditoria(RepositorioAuditoria repositorio, Clock reloj) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }

    public RegistroAuditoria registrar(String entidad, String entidadId, String estadoAnterior, String estadoNuevo,
            String usuario) {
        String quien = (usuario == null || usuario.isBlank()) ? "sistema" : usuario;
        return repositorio.save(new RegistroAuditoria(entidad, entidadId, estadoAnterior, estadoNuevo, quien,
                LocalDateTime.now(reloj)));
    }
}
