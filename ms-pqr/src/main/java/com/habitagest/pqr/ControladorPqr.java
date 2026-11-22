package com.habitagest.pqr;

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

/** Endpoints de contracts/pqr.yaml (REQ-PH-025 a REQ-PH-030). */
@RestController
public class ControladorPqr {

    private final ServicioPqr servicio;

    public ControladorPqr(ServicioPqr servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/pqr")
    @ResponseStatus(HttpStatus.CREATED)
    public Pqr radicar(@Valid @RequestBody PqrNueva cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.radicar(cuerpo.residenteId(), cuerpo.asunto(), cuerpo.descripcion(), usuario);
    }

    @GetMapping("/pqr")
    public List<Pqr> listar(@RequestParam(required = false) String residenteId) {
        return servicio.listar(residenteId);
    }

    @GetMapping("/pqr/{id}")
    public Pqr consultar(@PathVariable String id) {
        return servicio.consultar(id);
    }

    @PostMapping("/pqr/{id}/estado")
    public Pqr cambiarEstado(@PathVariable String id, @Valid @RequestBody CambioEstado cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.cambiarEstado(id, cuerpo.estado(), usuario);
    }

    @PostMapping("/pqr/{id}/cierre")
    public Pqr cerrar(@PathVariable String id, @Valid @RequestBody CierrePqr cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.cerrar(id, cuerpo.respuesta(), usuario);
    }

    @GetMapping("/pqr/{id}/auditoria")
    public List<Cambio> auditoria(@PathVariable String id) {
        return servicio.auditoria(id);
    }
}
