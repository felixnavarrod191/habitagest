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

    /** Solo propietarios y residentes: el apartamento que el administrador del sistema les asignó. */
    private String apartamentoId;

    /** Texto del apartamento asignado (por ejemplo "Torre 1 · Apto 102"), para mostrarlo en el menú. */
    private String apartamentoNombre;

    /** Solo propietarios y residentes: su nombre completo y su documento, los mismos que se registran en copropiedad. */
    private String nombreCompleto;
    private String documento;

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

    public String getApartamentoId() {
        return apartamentoId;
    }

    public void setApartamentoId(String apartamentoId) {
        this.apartamentoId = apartamentoId;
    }

    public String getApartamentoNombre() {
        return apartamentoNombre;
    }

    public void setApartamentoNombre(String apartamentoNombre) {
        this.apartamentoNombre = apartamentoNombre;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
