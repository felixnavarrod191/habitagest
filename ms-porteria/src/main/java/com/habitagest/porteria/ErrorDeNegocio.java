package com.habitagest.porteria;

/** Error con su código HTTP y su código de contrato (E400, E403, E404, E409, E503). */
public class ErrorDeNegocio extends RuntimeException {

    private final int estado;
    private final String codigo;

    private ErrorDeNegocio(int estado, String codigo, String mensaje) {
        super(mensaje);
        this.estado = estado;
        this.codigo = codigo;
    }

    public static ErrorDeNegocio solicitudInvalida(String mensaje) {
        return new ErrorDeNegocio(400, "E400", mensaje);
    }

    public static ErrorDeNegocio sinPermiso(String mensaje) {
        return new ErrorDeNegocio(403, "E403", mensaje);
    }

    public static ErrorDeNegocio noEncontrado(String mensaje) {
        return new ErrorDeNegocio(404, "E404", mensaje);
    }

    public static ErrorDeNegocio reglaDeNegocio(String mensaje) {
        return new ErrorDeNegocio(409, "E409", mensaje);
    }

    public static ErrorDeNegocio servicioNoDisponible(String mensaje) {
        return new ErrorDeNegocio(503, "E503", mensaje);
    }

    public int estado() {
        return estado;
    }

    public String codigo() {
        return codigo;
    }
}
