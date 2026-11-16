package com.habitagest.reservas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Zona común con su horario de uso, en horas de 24 con el formato HH:mm. */
@Document(collection = "zonas")
public class Zona {

    @Id
    private String id;
    private String nombre;
    private String horaInicio;
    private String horaFin;

    public Zona() {
    }

    public Zona(String nombre, String horaInicio, String horaFin) {
        this.nombre = nombre;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }
}
