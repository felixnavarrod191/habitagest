package com.habitagest.web;

import com.habitagest.web.seguridad.Rol;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Inicio de cada área por rol. La funcionalidad de negocio de cada área llega en los sprints siguientes. */
@Controller
public class ControladorAreas {

    @GetMapping("/sistema")
    public String sistema(Model modelo) {
        return area(modelo, Rol.ADMIN_SISTEMA, "Usuarios, roles y parámetros.");
    }

    @GetMapping("/administracion")
    public String administracion(Model modelo) {
        return area(modelo, Rol.ADMIN_CONJUNTO, "Unidades, cartera, comunicados, PQR y órdenes de mantenimiento.");
    }

    @GetMapping("/consejo")
    public String consejo(Model modelo) {
        return area(modelo, Rol.CONSEJO, "Aprobación de gastos sobre el tope y resúmenes.");
    }

    @GetMapping("/propietario")
    public String propietario(Model modelo) {
        return area(modelo, Rol.PROPIETARIO, "Estado de cuenta, reservas y PQR.");
    }

    @GetMapping("/residente")
    public String residente(Model modelo) {
        return area(modelo, Rol.RESIDENTE, "Reservas, visitas autorizadas y PQR.");
    }

    @GetMapping("/porteria")
    public String porteria(Model modelo) {
        return area(modelo, Rol.PORTERO, "Ingreso y salida de visitantes y paquetes.");
    }

    @GetMapping("/vigilancia")
    public String vigilancia(Model modelo) {
        return area(modelo, Rol.VIGILANTE, "Novedades de seguridad.");
    }

    @GetMapping("/contabilidad")
    public String contabilidad(Model modelo) {
        return area(modelo, Rol.CONTADOR, "Pagos, mora y cartera.");
    }

    @GetMapping("/revision")
    public String revision(Model modelo) {
        return area(modelo, Rol.REVISOR_FISCAL, "Consulta en solo lectura y observaciones de auditoría.");
    }

    @GetMapping("/mantenimiento")
    public String mantenimiento(Model modelo) {
        return area(modelo, Rol.ENCARGADO_MANTENIMIENTO, "Ejecución de órdenes y materiales.");
    }

    @GetMapping("/proveedor")
    public String proveedor(Model modelo) {
        return area(modelo, Rol.PROVEEDOR, "Órdenes de mantenimiento asignadas.");
    }

    private String area(Model modelo, Rol rol, String descripcion) {
        modelo.addAttribute("titulo", rol.etiqueta());
        modelo.addAttribute("descripcion", descripcion);
        return "area";
    }
}
