package com.habitagest.porteria;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Una novedad de seguridad con su fecha, hora y autor (REQ-PH-037). */
@Document(collection = "novedades")
public class Novedad {

    @Id
    private String id;
    private String texto;
    private String autor;
    private LocalDateTime fecha;

    public Novedad() {
    }

    public Novedad(String texto, String autor, LocalDateTime fecha) {
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
