package com.habitagest.finanzas;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** REQ-PH-044: observación de auditoría sobre un pago o una orden, con su fecha y su autor. */
@Document(collection = "observaciones")
public class Observacion {

    @Id
    private String id;
    private String referencia;
    private String texto;
    private String autor;
    private LocalDateTime fecha;

    public Observacion() {
    }

    public Observacion(String referencia, String texto, String autor, LocalDateTime fecha) {
        this.referencia = referencia;
        this.texto = texto;
        this.autor = autor;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferencia() {
        return referencia;
    }

    public String getTexto() {
        return texto;
    }

    public String getAutor() {
        return autor;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
