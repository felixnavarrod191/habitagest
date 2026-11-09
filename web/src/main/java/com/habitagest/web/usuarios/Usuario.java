package com.habitagest.web.usuarios;

import com.habitagest.web.seguridad.Rol;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("usuarios")
public class Usuario {

    @Id
    private String id;

    @Indexed(unique = true)
    private String usuario;

    private String contrasenaCifrada;
    private Rol rol;
    private boolean activo;

    public Usuario(String usuario, String contrasenaCifrada, Rol rol, boolean activo) {
        this.usuario = usuario;
        this.contrasenaCifrada = contrasenaCifrada;
        this.rol = rol;
        this.activo = activo;
    }

    public String getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContrasenaCifrada() {
        return contrasenaCifrada;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
