package com.habitagest.copropiedad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "residentes")
public class Residente {

    @Id
    private String id;
    private String nombre;
    private String documento;
    private String apartamentoId;

    public Residente() {
    }

    public Residente(String nombre, String documento, String apartamentoId) {
        this.nombre = nombre;
        this.documento = documento;
        this.apartamentoId = apartamentoId;
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

    public String getDocumento() {
        return documento;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }
}
