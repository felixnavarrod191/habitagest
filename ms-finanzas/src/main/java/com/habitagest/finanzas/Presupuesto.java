package com.habitagest.finanzas;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "presupuestos")
public class Presupuesto {

    @Id
    private String id;
    private String mes;
    private double valor;

    public Presupuesto() {
    }

    public Presupuesto(String mes, double valor) {
        this.mes = mes;
        this.valor = valor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMes() {
        return mes;
    }

    public double getValor() {
        return valor;
    }
}
