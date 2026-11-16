package com.habitagest.copropiedad;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de contracts/copropiedad.yaml (REQ-PH-004 a REQ-PH-007). */
@RestController
public class ControladorCopropiedad {

    private final ServicioCopropiedad servicio;

    public ControladorCopropiedad(ServicioCopropiedad servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/torres")
    public List<Torre> torres() {
        return servicio.listarTorres();
    }

    @PostMapping("/torres")
    @ResponseStatus(HttpStatus.CREATED)
    public Torre crearTorre(@Valid @RequestBody TorreNueva cuerpo) {
        return servicio.crearTorre(cuerpo.nombre());
    }

    @GetMapping("/apartamentos")
    public List<Apartamento> apartamentos(@RequestParam(required = false) String torre) {
        return servicio.listarApartamentos(torre);
    }

    @PostMapping("/apartamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public Apartamento crearApartamento(@Valid @RequestBody ApartamentoNuevo cuerpo) {
        return servicio.crearApartamento(cuerpo.torre(), cuerpo.numero(), cuerpo.coeficiente());
    }

    @GetMapping("/apartamentos/{id}")
    public Apartamento apartamento(@PathVariable String id) {
        return servicio.obtenerApartamento(id);
    }

    @GetMapping("/apartamentos/{id}/residentes")
    public List<Residente> residentes(@PathVariable String id) {
        return servicio.listarResidentes(id);
    }

    @PostMapping("/propietarios")
    @ResponseStatus(HttpStatus.CREATED)
    public Propietario crearPropietario(@Valid @RequestBody PersonaNueva cuerpo) {
        return servicio.crearPropietario(cuerpo.nombre(), cuerpo.documento(), cuerpo.apartamentoId());
    }

    @GetMapping("/propietarios/{id}")
    public Propietario propietario(@PathVariable String id) {
        return servicio.obtenerPropietario(id);
    }

    @PostMapping("/residentes")
    @ResponseStatus(HttpStatus.CREATED)
    public Residente crearResidente(@Valid @RequestBody PersonaNueva cuerpo) {
        return servicio.crearResidente(cuerpo.nombre(), cuerpo.documento(), cuerpo.apartamentoId());
    }
}
