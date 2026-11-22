package com.habitagest.porteria;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** El ingreso o la salida de un visitante, con su hora y quién lo registró (REQ-PH-034). */
@Document(collection = "ingresos")
public class Ingreso {

    @Id
    private String id;
    private String visitaId;
    private String tipo;
    private LocalDateTime hora;
    private String registradoPor;

    public Ingreso() {
    }

    public Ingreso(String visitaId, String tipo, LocalDateTime hora, String registradoPor) {
        this.visitaId = visitaId;
        this.tipo = tipo;
        this.hora = hora;
        this.registradoPor = registradoPor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisitaId() {
        return visitaId;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getHora() {
        return hora;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }
}
