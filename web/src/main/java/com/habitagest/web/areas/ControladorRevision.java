package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.pantallas.PantallasFinanzas;
import com.habitagest.web.pantallas.Tabla;
import com.habitagest.web.seguridad.Rol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Área del revisor fiscal: consulta cartera y estados de cuenta en solo lectura y deja observaciones de auditoría
 * (REQ-PH-044); el servicio de finanzas rechaza cualquier otra escritura suya.
 */
@Controller
public class ControladorRevision extends ControladorBase {

    private static final Rol ROL = Rol.REVISOR_FISCAL;

    public ControladorRevision(ClienteApi api) {
        super(api);
    }

    /** La tabla de observaciones de auditoría; la usan el revisor fiscal y el administrador del conjunto. */
    public static Tabla tablaObservaciones(List<JsonNode> observaciones) {
        List<Fila> filas = new ArrayList<>();
        for (JsonNode observacion : observaciones) {
            filas.add(fila(celdas(fechaHora(t(observacion, "fecha")), t(observacion, "autor"),
                    t(observacion, "referencia"), t(observacion, "texto"))));
        }
        return tabla("Observaciones de auditoría", "Todavía no hay observaciones.",
                columnas("Fecha y hora", "Autor", "Sobre", "Observación"), filas);
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

    @GetMapping("/revision/observaciones")
    public String observaciones(Model modelo) {
        Carga carga = carga();
        return mostrar(modelo, ROL, "/revision/observaciones", "Observaciones de auditoría",
                "Cada observación queda con su fecha y con quien la dejó; el administrador del conjunto la consulta.",
                carga,
                List.of(post("Dejar una observación", "/revision/observaciones", "Guardar",
                        seleccion("tipo", "Sobre",
                                opciones("Pago", "Pago", "Orden de mantenimiento", "Orden de mantenimiento")),
                        texto("identificador", "Identificador o descripción"),
                        area("texto", "Qué observas"))),
                List.of(tablaObservaciones(carga.lista("/finanzas/observaciones"))));
    }

    @PostMapping("/revision/observaciones")
    public String dejarObservacion(@RequestParam String tipo, @RequestParam String identificador,
            @RequestParam String texto, RedirectAttributes redireccion) {
        String referencia = limpio(tipo) + " " + limpio(identificador);
        return hacer(redireccion, "/revision/observaciones", "Observación guardada.",
                () -> api.post("/finanzas/observaciones", Map.of("referencia", referencia, "texto", limpio(texto))));
    }
}
