package com.habitagest.porteria;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Un paquete recibido en portería para un apartamento (REQ-PH-036). */
@Document(collection = "paquetes")
public class Paquete {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ENTREGADO = "ENTREGADO";

    @Id
    private String id;
    private String apartamentoId;
    private String descripcion;
    private String estado;
    private LocalDateTime recibido;

    public Paquete() {
    }

    public Paquete(String apartamentoId, String descripcion, LocalDateTime recibido) {
        this.apartamentoId = apartamentoId;
        this.descripcion = descripcion;
        this.recibido = recibido;
        this.estado = PENDIENTE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getApartamentoId() {
        return apartamentoId;
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

    public LocalDateTime getRecibido() {
        return recibido;
    }
}
