package com.habitagest.pqr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Un aviso publicado a la comunidad mientras esté vigente. */
@Document(collection = "comunicados")
public class Comunicado {

    @Id
    private String id;
    private String titulo;
    private String texto;
    private LocalDate vigenteHasta;
    private LocalDateTime publicado;

    public Comunicado() {
    }

    public Comunicado(String titulo, String texto, LocalDate vigenteHasta, LocalDateTime publicado) {
        this.titulo = titulo;
        this.texto = texto;
        this.vigenteHasta = vigenteHasta;
        this.publicado = publicado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDate getVigenteHasta() {
        return vigenteHasta;
    }

    public LocalDateTime getPublicado() {
        return publicado;
    }
}
