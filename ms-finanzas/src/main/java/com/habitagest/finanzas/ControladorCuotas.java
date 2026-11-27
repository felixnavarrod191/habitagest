package com.habitagest.finanzas;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de contracts/finanzas.yaml para REQ-PH-008 a REQ-PH-010. */
@RestController
public class ControladorCuotas {

    private final ServicioCuotas servicio;

    public ControladorCuotas(ServicioCuotas servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/presupuestos")
    @ResponseStatus(HttpStatus.CREATED)
    public Presupuesto guardarPresupuesto(@Valid @RequestBody PresupuestoNuevo cuerpo) {
        return servicio.guardarPresupuesto(cuerpo.mes(), cuerpo.valor());
    }

    @GetMapping("/presupuestos")
    public Presupuesto presupuesto(@RequestParam String mes) {
        return servicio.consultarPresupuesto(mes);
    }

    @GetMapping("/presupuestos/todos")
    public List<Presupuesto> todosLosPresupuestos() {
        return servicio.listarPresupuestos();
    }

    @PostMapping("/cuotas/generaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public List<CuotaVista> generar(@Valid @RequestBody GeneracionCuotas cuerpo) {
        return servicio.generarCuotas(cuerpo.mes());
    }

    @GetMapping("/cuotas")
    public List<CuotaVista> cuotas(@RequestParam(required = false) String apartamentoId,
            @RequestParam(required = false) String mes) {
        return servicio.consultarCuotas(apartamentoId, mes);
    }
}
