package com.habitagest.mantenimiento;

/** Un material usado en la ejecución de una orden, con su valor en pesos. */
public class Material {

    private String descripcion;
    private long valor;

    public Material() {
    }

    public Material(String descripcion, long valor) {
        this.descripcion = descripcion;
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public long getValor() {
        return valor;
    }
}
