package com.habitagest.web.seguridad;

/** Los 11 roles del sistema. Cada uno tiene su área, que empieza en {@code rutaBase()}. */
public enum Rol {

    ADMIN_SISTEMA("Administrador del sistema", "/sistema"),
    ADMIN_CONJUNTO("Administrador del conjunto", "/administracion"),
    CONSEJO("Consejo de administración", "/consejo"),
    PROPIETARIO("Propietario", "/propietario"),
    RESIDENTE("Residente", "/residente"),
    PORTERO("Portero", "/porteria"),
    VIGILANTE("Vigilante", "/vigilancia"),
    CONTADOR("Contador", "/contabilidad"),
    REVISOR_FISCAL("Revisor fiscal", "/revision"),
    ENCARGADO_MANTENIMIENTO("Encargado de mantenimiento", "/mantenimiento"),
    PROVEEDOR("Proveedor", "/proveedor");

    private final String etiqueta;
    private final String rutaBase;

    Rol(String etiqueta, String rutaBase) {
        this.etiqueta = etiqueta;
        this.rutaBase = rutaBase;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public String rutaBase() {
        return rutaBase;
    }
}
