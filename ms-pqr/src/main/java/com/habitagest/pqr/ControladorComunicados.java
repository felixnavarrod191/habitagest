package com.habitagest.pqr;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de comunicados de contracts/pqr.yaml (REQ-PH-031 y REQ-PH-032). */
@RestController
public class ControladorComunicados {

    private final ServicioComunicados servicio;

    public ControladorComunicados(ServicioComunicados servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/comunicados")
    @ResponseStatus(HttpStatus.CREATED)
    public Comunicado publicar(@Valid @RequestBody ComunicadoNuevo cuerpo) {
        return servicio.publicar(cuerpo.titulo(), cuerpo.texto(), cuerpo.vigenteHasta());
    }

    @GetMapping("/comunicados")
    public List<Comunicado> vigentes() {
        return servicio.vigentes();
    }
}
