package com.habitagest.pqr;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Una petición, queja o reclamo con su flujo de estados (RN-08). Las fechas son hora de Colombia. */
@Document(collection = "pqr")
public class Pqr {

    public static final String RADICADA = "RADICADA";
    public static final String EN_REVISION = "EN_REVISION";
    public static final String EN_GESTION = "EN_GESTION";
    public static final String RESUELTA = "RESUELTA";
    public static final String CERRADA = "CERRADA";

    @Id
    private String id;
    private String numero;
    private String residenteId;
    private String asunto;
    private String descripcion;
    private String estado;
    private String respuesta;
    private LocalDateTime fecha;
    private List<Cambio> historial = new ArrayList<>();

    public Pqr() {
    }

    public Pqr(String numero, String residenteId, String asunto, String descripcion, LocalDateTime fecha) {
        this.numero = numero;
        this.residenteId = residenteId;
        this.asunto = asunto;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estado = RADICADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public String getResidenteId() {
        return residenteId;
    }

    public String getAsunto() {
        return asunto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<Cambio> getHistorial() {
        return historial;
    }
}
