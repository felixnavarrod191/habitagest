package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Opcion;
import com.habitagest.web.pantallas.PantallasFinanzas;
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

/** Área del contador: presupuesto, cuotas, mora, pagos y cartera. */
@Controller
public class ControladorContabilidad extends ControladorBase {

    private static final Rol ROL = Rol.CONTADOR;

    public ControladorContabilidad(ClienteApi api) {
        super(api);
    }

    @GetMapping("/contabilidad/cuotas")
    public String cuotas(Model modelo) {
        Carga carga = carga();
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        return mostrar(modelo, ROL, "/contabilidad/cuotas", "Cuotas y mora",
                "Presupuesto del mes, cuotas por coeficiente y mora del 2 % mensual.", carga,
                List.of(post("Guardar el presupuesto del mes", "/contabilidad/presupuestos", "Guardar",
                                mes("mes", "Mes"), numero("valor", "Valor del presupuesto ($)")),
                        post("Generar las cuotas del mes", "/contabilidad/cuotas", "Generar cuotas",
                                mes("mes", "Mes")),
                        post("Aplicar la mora a las cuotas vencidas", "/contabilidad/mora", "Aplicar mora")),
                List.of(PantallasFinanzas.cuotas("Cuotas generadas", carga.lista("/finanzas/cuotas"), apartamentos)));
    }

    @PostMapping("/contabilidad/presupuestos")
    public String presupuesto(@RequestParam String mes, @RequestParam String valor, RedirectAttributes redireccion) {
        return hacer(redireccion, "/contabilidad/cuotas", "Presupuesto guardado.",
                () -> api.post("/finanzas/presupuestos", Map.of("mes", limpio(mes), "valor", numero(valor))));
    }

    @PostMapping("/contabilidad/cuotas")
    public String generar(@RequestParam String mes, RedirectAttributes redireccion) {
        return hacer(redireccion, "/contabilidad/cuotas", "Cuotas generadas.",
                () -> api.post("/finanzas/cuotas/generaciones", Map.of("mes", limpio(mes))));
    }

    @PostMapping("/contabilidad/mora")
    public String mora(RedirectAttributes redireccion) {
        return hacer(redireccion, "/contabilidad/cuotas", "Mora aplicada a las cuotas vencidas.",
                () -> api.post("/finanzas/mora/aplicaciones", Map.of()));
    }

    @GetMapping("/contabilidad/pagos")
    public String pagos(Model modelo) {
        Carga carga = carga();
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<JsonNode> cuotas = carga.lista("/finanzas/cuotas");
        List<Opcion> pendientes = new ArrayList<>();
        for (JsonNode cuota : cuotas) {
            if (d(cuota, "saldo") > 0) {
                pendientes.add(new Opcion(t(cuota, "id"), PantallasFinanzasNombre.de(apartamentos, cuota) + " · "
                        + t(cuota, "mes") + " · saldo " + pesos(d(cuota, "saldo"))));
            }
        }
        return mostrar(modelo, ROL, "/contabilidad/pagos", "Pagos",
                "Registra el pago total o parcial de una cuota; el saldo y el estado se actualizan solos.", carga,
                List.of(post("Registrar un pago", "/contabilidad/pagos", "Registrar pago",
                        seleccion("cuotaId", "Cuota", pendientes), numero("valor", "Valor pagado ($)"))),
                List.of(PantallasFinanzas.cuotasPorPagar("/contabilidad/pagos", cuotas, apartamentos)));
    }

    @PostMapping("/contabilidad/pagos")
    public String pagar(@RequestParam String cuotaId, @RequestParam String valor, RedirectAttributes redireccion) {
        return hacer(redireccion, "/contabilidad/pagos", "Pago registrado.",
                () -> api.post("/finanzas/pagos", Map.of("cuotaId", cuotaId, "valor", numero(valor))));
    }

    @GetMapping("/contabilidad/cartera")
    public String cartera(Model modelo) {
        Carga carga = carga();
        return mostrar(modelo, ROL, "/contabilidad/cartera", "Cartera",
                "Saldo y mora por apartamento.", carga, List.of(),
                List.of(PantallasFinanzas.cartera(carga.lista("/finanzas/cartera"))));
    }

    /** Nombre legible del apartamento de una cuota. */
    static final class PantallasFinanzasNombre {
        private PantallasFinanzasNombre() {
        }

        static String de(List<JsonNode> apartamentos, JsonNode cuota) {
            for (JsonNode apartamento : apartamentos) {
                if (t(cuota, "apartamentoId").equals(t(apartamento, "id"))) {
                    return nombreApartamento(apartamento);
                }
            }
            return t(cuota, "apartamentoId");
        }
    }
}
