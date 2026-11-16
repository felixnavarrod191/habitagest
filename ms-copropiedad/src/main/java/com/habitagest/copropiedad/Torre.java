package com.habitagest.copropiedad;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "torres")
public class Torre {

    @Id
    private String id;
    private String nombre;

    public Torre() {
    }

    public Torre(String nombre) {
        this.nombre = nombre;
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
}
