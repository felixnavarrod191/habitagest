package com.habitagest.finanzas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint de contracts/finanzas.yaml para REQ-PH-043. */
@RestController
public class ControladorResumen {

    private final ServicioResumen servicio;

    public ControladorResumen(ServicioResumen servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/resumen")
    public ResumenFinanciero resumen(@RequestParam String mes) {
        return servicio.resumen(mes);
    }
}
