package com.habitagest.finanzas;

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

/** Endpoints de contracts/finanzas.yaml para REQ-PH-011 a REQ-PH-017. */
@RestController
public class ControladorPagos {

    private final ServicioPagos servicio;

    public ControladorPagos(ServicioPagos servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/pagos")
    @ResponseStatus(HttpStatus.CREATED)
    public CuotaVista registrarPago(@Valid @RequestBody PagoNuevo cuerpo,
            @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario) {
        return servicio.registrarPago(cuerpo.cuotaId(), cuerpo.valor(), usuario);
    }

    @PostMapping("/mora/aplicaciones")
    public List<CuotaVista> aplicarMora(@RequestBody(required = false) AplicacionMora cuerpo) {
        return servicio.aplicarMora(cuerpo == null ? null : cuerpo.mes());
    }

    @GetMapping("/apartamentos/{id}/mora")
    public EstadoMora estadoMora(@PathVariable String id) {
        return servicio.estadoMora(id);
    }

    @GetMapping("/cartera")
    public List<CarteraItem> cartera(@RequestParam(required = false) String torre) {
        return servicio.cartera(torre);
    }

    @GetMapping("/estado-de-cuenta/{apartamentoId}")
    public EstadoCuenta estadoDeCuenta(@PathVariable String apartamentoId) {
        return servicio.estadoDeCuenta(apartamentoId);
    }
}
