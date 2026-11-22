package com.habitagest.porteria;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Una visita autorizada por un residente para una fecha (RN-10). */
@Document(collection = "visitas")
public class Visita {

    public static final String AUTORIZADA = "AUTORIZADA";
    public static final String DENTRO = "DENTRO";
    public static final String SALIO = "SALIO";

    @Id
    private String id;
    private String apartamentoId;
    private String nombre;
    private String documento;
    private LocalDate fecha;
    private String estado;

    public Visita() {
    }

    public Visita(String apartamentoId, String nombre, String documento, LocalDate fecha) {
        this.apartamentoId = apartamentoId;
        this.nombre = nombre;
        this.documento = documento;
        this.fecha = fecha;
        this.estado = AUTORIZADA;
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

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
