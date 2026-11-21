package com.habitagest.reservas;

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

/** Endpoints de contracts/reservas.yaml (REQ-PH-018 a REQ-PH-024). */
@RestController
public class ControladorReservas {

    private final ServicioReservas servicio;

    public ControladorReservas(ServicioReservas servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/zonas")
    @ResponseStatus(HttpStatus.CREATED)
    public Zona crearZona(@Valid @RequestBody ZonaNueva cuerpo) {
        return servicio.crearZona(cuerpo.nombre(), cuerpo.horaInicio(), cuerpo.horaFin());
    }

    @GetMapping("/zonas")
    public List<Zona> zonas() {
        return servicio.listarZonas();
    }

    @GetMapping("/zonas/{id}/disponibilidad")
    public List<Horario> disponibilidad(@PathVariable String id, @RequestParam String fecha) {
        return servicio.disponibilidad(id, fecha);
    }

    @GetMapping("/reservas")
    public List<Reserva> reservas(@RequestParam(required = false) String apartamentoId) {
        return servicio.listarReservas(apartamentoId);
    }

    @PostMapping("/reservas")
    @ResponseStatus(HttpStatus.CREATED)
    public Reserva reservar(@Valid @RequestBody ReservaNueva cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.reservar(cuerpo.zonaId(), cuerpo.apartamentoId(), cuerpo.inicio(), cuerpo.fin(), usuario);
    }

    @PostMapping("/reservas/{id}/cancelaciones")
    public Reserva cancelar(@PathVariable String id, @Valid @RequestBody Cancelacion cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.cancelar(id, cuerpo.solicitante(), usuario);
    }
}
