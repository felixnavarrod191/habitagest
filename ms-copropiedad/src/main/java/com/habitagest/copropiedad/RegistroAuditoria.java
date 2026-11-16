package com.habitagest.copropiedad;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** NFR-PH-003: cada cambio de estado queda con su usuario y su fecha-hora. */
@Document(collection = "auditoria")
public class RegistroAuditoria {

    @Id
    private String id;
    private String entidad;
    private String entidadId;
    private String estadoAnterior;
    private String estadoNuevo;
    private String usuario;
    private LocalDateTime fecha;

    public RegistroAuditoria() {
    }

    public RegistroAuditoria(String entidad, String entidadId, String estadoAnterior, String estadoNuevo,
            String usuario, LocalDateTime fecha) {
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntidad() {
        return entidad;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
