package com.habitagest.finanzas;

import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Cuota ordinaria de un apartamento. El saldo incluye la mora acumulada. */
@Document(collection = "cuotas")
public class Cuota {

    @Id
    private String id;
    private String apartamentoId;
    private String mes;
    private double valor;
    private double saldo;
    private double mora;
    private LocalDate vencimiento;
    private String moraAplicadaMes;

    public Cuota() {
    }

    public Cuota(String apartamentoId, String mes, double valor, LocalDate vencimiento) {
        this.apartamentoId = apartamentoId;
        this.mes = mes;
        this.valor = valor;
        this.saldo = valor;
        this.mora = 0;
        this.vencimiento = vencimiento;
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

    public String getMes() {
        return mes;
    }

    public double getValor() {
        return valor;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getMora() {
        return mora;
    }

    public void setMora(double mora) {
        this.mora = mora;
    }

    public LocalDate getVencimiento() {
        return vencimiento;
    }

    public String getMoraAplicadaMes() {
        return moraAplicadaMes;
    }

    public void setMoraAplicadaMes(String moraAplicadaMes) {
        this.moraAplicadaMes = moraAplicadaMes;
    }
}
