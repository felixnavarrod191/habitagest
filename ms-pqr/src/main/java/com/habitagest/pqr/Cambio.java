package com.habitagest.pqr;

import java.time.LocalDateTime;

/** Un cambio de estado de una PQR, con quién lo hizo y cuándo (REQ-PH-026 y REQ-PH-030). */
public class Cambio {

    private String estadoAnterior;
    private String estadoNuevo;
    private String usuario;
    private LocalDateTime fecha;

    public Cambio() {
    }

    public Cambio(String estadoAnterior, String estadoNuevo, String usuario, LocalDateTime fecha) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
