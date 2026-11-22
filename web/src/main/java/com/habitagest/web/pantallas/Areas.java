package com.habitagest.web.pantallas;

import com.habitagest.web.seguridad.Rol;
import java.util.List;

/** El menú de cada una de las 11 áreas: las pantallas que tiene cada rol. */
public final class Areas {

    private Areas() {
    }

    public static List<Enlace> menu(Rol rol) {
        return switch (rol) {
            case ADMIN_SISTEMA -> List.of(
                    new Enlace("Usuarios", "/sistema/usuarios", "bi-people-fill", "Crear usuarios, activarlos y ver los 11 roles."),
                    new Enlace("Parámetros", "/sistema/parametros", "bi-sliders", "Valores de negocio que usa el sistema."));
            case ADMIN_CONJUNTO -> List.of(
                    new Enlace("Unidades", "/administracion/copropiedad", "bi-building", "Torres, apartamentos con su coeficiente y quién vive en cada uno."),
                    new Enlace("Cuotas y cartera", "/administracion/finanzas", "bi-cash-stack", "Presupuesto, cuotas, mora y cartera."),
                    new Enlace("Reservas", "/administracion/reservas", "bi-calendar-check", "Zonas comunes y cancelación de reservas."),
                    new Enlace("PQR", "/administracion/pqr", "bi-chat-left-text", "Gestionar y cerrar las PQR."),
                    new Enlace("Comunicados", "/administracion/comunicados", "bi-megaphone", "Publicar y consultar comunicados."),
                    new Enlace("Mantenimiento", "/administracion/mantenimiento", "bi-tools", "Crear y aprobar órdenes."),
                    new Enlace("Novedades", "/administracion/novedades", "bi-shield-check", "Novedades de seguridad registradas."));
            case CONSEJO -> List.of(
                    new Enlace("Aprobar órdenes", "/consejo/ordenes", "bi-check2-square", "Órdenes sobre el tope de $2.000.000."),
                    new Enlace("Resumen", "/consejo/resumen", "bi-graph-up", "Cartera y gastos de mantenimiento."),
                    new Enlace("Comunicados", "/consejo/comunicados", "bi-megaphone", "Comunicados vigentes."));
            case PROPIETARIO -> List.of(
                    new Enlace("Estado de cuenta", "/propietario/estado-de-cuenta", "bi-receipt", "Cuotas, pagos, saldo y mora de tu apartamento."),
                    new Enlace("Reservas", "/propietario/reservas", "bi-calendar-check", "Reservar zonas comunes y ver tus reservas."),
                    new Enlace("PQR", "/propietario/pqr", "bi-chat-left-text", "Radicar y seguir tus PQR."),
                    new Enlace("Comunicados", "/propietario/comunicados", "bi-megaphone", "Comunicados vigentes."),
                    new Enlace("Paquetes", "/propietario/paquetes", "bi-box-seam", "Paquetes de tu apartamento."));
            case RESIDENTE -> List.of(
                    new Enlace("Reservas", "/residente/reservas", "bi-calendar-check", "Reservar zonas comunes y ver tus reservas."),
                    new Enlace("Visitas", "/residente/visitas", "bi-person-plus", "Autorizar la visita de una persona."),
                    new Enlace("PQR", "/residente/pqr", "bi-chat-left-text", "Radicar y seguir tus PQR."),
                    new Enlace("Comunicados", "/residente/comunicados", "bi-megaphone", "Comunicados vigentes."),
                    new Enlace("Paquetes", "/residente/paquetes", "bi-box-seam", "Paquetes de tu apartamento."));
            case PORTERO -> List.of(
                    new Enlace("Visitas", "/porteria/visitas", "bi-door-open", "Visitas autorizadas, ingresos y salidas."),
                    new Enlace("Paquetes", "/porteria/paquetes", "bi-box-seam", "Recibir y entregar paquetes."));
            case VIGILANTE -> List.of(
                    new Enlace("Novedades", "/vigilancia/novedades", "bi-shield-check", "Registrar novedades de seguridad."));
            case CONTADOR -> List.of(
                    new Enlace("Cuotas y mora", "/contabilidad/cuotas", "bi-cash-stack", "Presupuesto, cuotas y mora."),
                    new Enlace("Pagos", "/contabilidad/pagos", "bi-wallet2", "Registrar pagos de las cuotas."),
                    new Enlace("Cartera", "/contabilidad/cartera", "bi-clipboard-data", "Saldo y mora por apartamento."));
            case REVISOR_FISCAL -> List.of(
                    new Enlace("Cartera", "/revision/cartera", "bi-clipboard-data", "Consulta en solo lectura."),
                    new Enlace("Estado de cuenta", "/revision/estado-de-cuenta", "bi-receipt", "Cuotas y pagos de un apartamento."));
            case ENCARGADO_MANTENIMIENTO -> List.of(
                    new Enlace("Órdenes", "/mantenimiento/ordenes", "bi-tools", "Ejecutar órdenes aprobadas y registrar materiales."));
            case PROVEEDOR -> List.of(
                    new Enlace("Mis órdenes", "/proveedor/ordenes", "bi-truck", "Órdenes asignadas a ti."));
        };
    }
}
