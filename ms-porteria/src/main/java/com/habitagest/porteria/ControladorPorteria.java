package com.habitagest.porteria;

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

/** Endpoints de contracts/porteria.yaml (REQ-PH-033 a REQ-PH-037). */
@RestController
public class ControladorPorteria {

    private final ServicioPorteria servicio;

    public ControladorPorteria(ServicioPorteria servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/visitas")
    @ResponseStatus(HttpStatus.CREATED)
    public Visita autorizar(@Valid @RequestBody VisitaNueva cuerpo) {
        return servicio.autorizar(cuerpo.apartamentoId(), cuerpo.nombre(), cuerpo.documento(), cuerpo.fecha());
    }

    @GetMapping("/visitas")
    public List<Visita> visitas(@RequestParam String fecha) {
        return servicio.visitasDeLaFecha(fecha);
    }

    @PostMapping("/ingresos")
    @ResponseStatus(HttpStatus.CREATED)
    public Ingreso registrar(@Valid @RequestBody IngresoNuevo cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.registrar(cuerpo.visitaId(), cuerpo.tipo(), usuario);
    }

    @PostMapping("/paquetes")
    @ResponseStatus(HttpStatus.CREATED)
    public Paquete registrarPaquete(@Valid @RequestBody PaqueteNuevo cuerpo) {
        return servicio.registrarPaquete(cuerpo.apartamentoId(), cuerpo.descripcion());
    }

    @GetMapping("/paquetes")
    public List<Paquete> paquetes(@RequestParam String apartamentoId) {
        return servicio.paquetesDelApartamento(apartamentoId);
    }

    @PostMapping("/paquetes/{id}/entrega")
    public Paquete entregar(@PathVariable String id,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.entregarPaquete(id, usuario);
    }

    @PostMapping("/novedades")
    @ResponseStatus(HttpStatus.CREATED)
    public Novedad registrarNovedad(@Valid @RequestBody NovedadNueva cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String autor) {
        return servicio.registrarNovedad(cuerpo.texto(), autor);
    }

    @GetMapping("/novedades")
    public List<Novedad> novedades() {
        return servicio.novedades();
    }
}
