package com.habitagest.reservas;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Las fechas son hora de Colombia, sin zona horaria. */
@Document(collection = "reservas")
public class Reserva {

    public static final String CONFIRMADA = "CONFIRMADA";
    public static final String CANCELADA = "CANCELADA";

    @Id
    private String id;
    private String zonaId;
    private String apartamentoId;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private String estado;

    public Reserva() {
    }

    public Reserva(String zonaId, String apartamentoId, LocalDateTime inicio, LocalDateTime fin) {
        this.zonaId = zonaId;
        this.apartamentoId = apartamentoId;
        this.inicio = inicio;
        this.fin = fin;
        this.estado = CONFIRMADA;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getZonaId() {
        return zonaId;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
