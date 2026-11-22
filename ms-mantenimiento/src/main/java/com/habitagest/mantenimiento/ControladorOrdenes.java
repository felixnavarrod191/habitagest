package com.habitagest.mantenimiento;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de contracts/mantenimiento.yaml (REQ-PH-038 a REQ-PH-042). */
@RestController
public class ControladorOrdenes {

    private final ServicioOrdenes servicio;

    public ControladorOrdenes(ServicioOrdenes servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/ordenes")
    @ResponseStatus(HttpStatus.CREATED)
    public Orden crear(@Valid @RequestBody OrdenNueva cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.crear(cuerpo.descripcion(), cuerpo.zona(), cuerpo.costoEstimado(), cuerpo.proveedorId(), usuario);
    }

    @GetMapping("/ordenes")
    public List<Orden> listar(@RequestParam(required = false) String estado,
            @RequestParam(required = false) String proveedorId) {
        return servicio.listar(estado, proveedorId);
    }

    @GetMapping("/ordenes/{id}")
    public Orden consultar(@PathVariable String id) {
        return servicio.consultar(id);
    }

    @PostMapping("/ordenes/{id}/aprobacion")
    public Orden aprobar(@PathVariable String id, @Valid @RequestBody Aprobacion cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.aprobar(id, cuerpo.aprobadoPor(), usuario);
    }

    @PostMapping("/ordenes/{id}/ejecucion")
    public Orden ejecutar(@PathVariable String id, @Valid @RequestBody Ejecucion cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        List<Material> materiales = cuerpo.materiales().stream()
                .map(material -> new Material(material.descripcion(), material.valor())).toList();
        return servicio.ejecutar(id, materiales, usuario);
    }

    @GetMapping("/ordenes/{id}/auditoria")
    public List<Cambio> auditoria(@PathVariable String id) {
        return servicio.auditoria(id);
    }

    @GetMapping("/gastos")
    public GastosMes gastos(@RequestParam String mes) {
        return servicio.gastos(mes);
    }
}
