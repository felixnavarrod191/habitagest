package com.habitagest.finanzas;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pagos")
public class Pago {

    @Id
    private String id;
    private String cuotaId;
    private String apartamentoId;
    private double valor;
    private LocalDateTime fecha;
    private String usuario;

    public Pago() {
    }

    public Pago(String cuotaId, String apartamentoId, double valor, LocalDateTime fecha, String usuario) {
        this.cuotaId = cuotaId;
        this.apartamentoId = apartamentoId;
        this.valor = valor;
        this.fecha = fecha;
        this.usuario = usuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuotaId() {
        return cuotaId;
    }

    public String getApartamentoId() {
        return apartamentoId;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getUsuario() {
        return usuario;
    }
}
