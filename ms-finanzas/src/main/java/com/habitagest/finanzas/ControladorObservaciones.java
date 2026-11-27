package com.habitagest.finanzas;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de contracts/finanzas.yaml para REQ-PH-044. */
@RestController
public class ControladorObservaciones {

    private final ServicioObservaciones servicio;

    public ControladorObservaciones(ServicioObservaciones servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/observaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public Observacion dejar(@Valid @RequestBody ObservacionNueva cuerpo,
            @RequestHeader(value = "X-Usuario", required = false) String usuario,
            @RequestHeader(value = "X-Rol", required = false) String rol) {
        return servicio.dejar(cuerpo.referencia(), cuerpo.texto(), usuario, rol);
    }

    @GetMapping("/observaciones")
    public List<Observacion> listar(@RequestHeader(value = "X-Rol", required = false) String rol) {
        return servicio.listar(rol);
    }
}
