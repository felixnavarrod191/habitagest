package com.habitagest.web.api;

/** Un servicio respondió con un error (o no se pudo conectar): trae el estado HTTP y el mensaje para el usuario. */
public class ErrorDelServicio extends RuntimeException {

    private final int estado;

    public ErrorDelServicio(int estado, String mensaje) {
        super(mensaje);
        this.estado = estado;
    }

    public int estado() {
        return estado;
    }
}
