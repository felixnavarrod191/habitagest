package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.PantallasFinanzas;
import com.habitagest.web.pantallas.Tabla;
import com.habitagest.web.seguridad.Rol;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Área del revisor fiscal: solo consulta; el servicio de finanzas rechaza cualquier escritura suya. */
@Controller
public class ControladorRevision extends ControladorBase {

    private static final Rol ROL = Rol.REVISOR_FISCAL;

    public ControladorRevision(ClienteApi api) {
        super(api);
    }

    @GetMapping("/revision/cartera")
    public String cartera(Model modelo) {
        Carga carga = carga();
        return mostrar(modelo, ROL, "/revision/cartera", "Cartera",
                "Consulta en solo lectura: el revisor fiscal no puede modificar nada.", carga, List.of(),
                List.of(PantallasFinanzas.cartera(carga.lista("/finanzas/cartera"))));
    }

    @GetMapping("/revision/estado-de-cuenta")
    public String estadoDeCuenta(@RequestParam(required = false) String apartamentoId, Model modelo) {
        Carga carga = carga();
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<Tabla> tablas = new ArrayList<>();
        if (apartamentoId != null && !apartamentoId.isBlank()) {
            JsonNode estado = carga.objeto("/finanzas/estado-de-cuenta/" + ClienteApi.codificar(apartamentoId));
            if (estado != null) {
                tablas.addAll(PantallasFinanzas.estadoDeCuenta(estado, apartamentos));
            }
        }
        return mostrar(modelo, ROL, "/revision/estado-de-cuenta", "Estado de cuenta",
                "Cuotas y pagos de un apartamento, en solo lectura.", carga,
                List.of(consulta("Elegir un apartamento", "/revision/estado-de-cuenta", "Consultar",
                        seleccion("apartamentoId", "Apartamento", opcionesDeApartamentos(apartamentos)))),
                tablas);
    }
}
