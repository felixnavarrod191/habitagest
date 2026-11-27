package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Accion;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.seguridad.Rol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Área del portero: visitas autorizadas con su ingreso y salida, y paquetes. */
@Controller
public class ControladorPorteriaWeb extends ControladorBase {

    private static final Rol ROL = Rol.PORTERO;

    public ControladorPorteriaWeb(ClienteApi api) {
        super(api);
    }

    @GetMapping("/porteria/visitas")
    public String visitas(@RequestParam(required = false) String fecha, Model modelo) {
        Carga carga = carga();
        String dia = fecha == null || fecha.isBlank() ? ControladorResidentes.hoy() : fecha;
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<Fila> filas = new ArrayList<>();
        Map<String, String> entradas = new HashMap<>();
        Map<String, String> salidas = new HashMap<>();
        for (JsonNode ingreso : carga.lista("/porteria/ingresos?fecha=" + ClienteApi.codificar(dia))) {
            ("ENTRADA".equals(t(ingreso, "tipo")) ? entradas : salidas).put(t(ingreso, "visitaId"), horaDe(ingreso));
        }
        for (JsonNode visita : carga.lista("/porteria/visitas?fecha=" + ClienteApi.codificar(dia))) {
            String estado = t(visita, "estado");
            Accion[] acciones = switch (estado) {
                case "AUTORIZADA" -> new Accion[] {accion("/porteria/visitas/ingreso", "Registrar entrada",
                        "visitaId", t(visita, "id"), "tipo", "ENTRADA", "fecha", dia)};
                case "DENTRO" -> new Accion[] {accion("/porteria/visitas/ingreso", "Registrar salida",
                        "visitaId", t(visita, "id"), "tipo", "SALIDA", "fecha", dia)};
                default -> new Accion[0];
            };
            filas.add(fila(celdas(t(visita, "nombre"), t(visita, "documento"),
                    nombreDeApartamento(apartamentos, t(visita, "apartamentoId")),
                    ControladorResidentes.estadoVisita(estado), entradas.getOrDefault(t(visita, "id"), "—"),
                    salidas.getOrDefault(t(visita, "id"), "—")), acciones));
        }
        return mostrar(modelo, ROL, "/porteria/visitas", "Visitas",
                "Registra la entrada y la salida de las visitas que autorizaron los residentes.", carga,
                List.of(consulta("Ver las visitas de un día", "/porteria/visitas", "Ver", fecha("fecha", "Día", dia))),
                List.of(tabla("Visitas del " + dia, "No hay visitas autorizadas ese día.",
                        columnas("Visitante", "Documento", "Apartamento", "Estado", "Entrada", "Salida"), filas)));
    }

    /** La hora de un ingreso o salida, por ejemplo "10:05", de "2026-11-26T10:05:33". */
    static String horaDe(JsonNode ingreso) {
        String iso = t(ingreso, "hora");
        return iso.length() >= 16 ? iso.substring(11, 16) : "—";
    }

    @PostMapping("/porteria/visitas/ingreso")
    public String ingreso(@RequestParam String visitaId, @RequestParam String tipo, @RequestParam String fecha,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/porteria/visitas?fecha=" + ClienteApi.codificar(fecha),
                "ENTRADA".equals(tipo) ? "Entrada registrada." : "Salida registrada.",
                () -> api.post("/porteria/ingresos", Map.of("visitaId", visitaId, "tipo", tipo)));
    }

    @GetMapping("/porteria/paquetes")
    public String paquetes(@RequestParam(required = false) String apartamentoId, Model modelo) {
        Carga carga = carga();
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<Fila> filas = new ArrayList<>();
        String titulo = "Paquetes";
        if (apartamentoId != null && !apartamentoId.isBlank()) {
            titulo = "Paquetes de " + nombreDeApartamento(apartamentos, apartamentoId);
            for (JsonNode paquete : carga.lista("/porteria/paquetes?apartamentoId="
                    + ClienteApi.codificar(apartamentoId))) {
                boolean pendiente = "PENDIENTE".equals(t(paquete, "estado"));
                filas.add(fila(celdas(t(paquete, "descripcion"), fechaHora(t(paquete, "recibido")),
                        pendiente ? "En portería" : "Entregado"),
                        pendiente ? new Accion[] {accion("/porteria/paquetes/entrega", "Entregar", "id",
                                t(paquete, "id"), "apartamentoId", apartamentoId)} : new Accion[0]));
            }
        }
        return mostrar(modelo, ROL, "/porteria/paquetes", "Paquetes",
                "Recibe los paquetes de cada apartamento y márcalos como entregados.", carga,
                List.of(post("Recibir un paquete", "/porteria/paquetes", "Registrar",
                                seleccion("apartamentoId", "Apartamento", opcionesDeApartamentos(apartamentos)),
                                texto("descripcion", "Descripción del paquete")),
                        consulta("Ver los paquetes de un apartamento", "/porteria/paquetes", "Ver",
                                seleccion("apartamentoId", "Apartamento", opcionesDeApartamentos(apartamentos)))),
                List.of(tabla(titulo, "Elige un apartamento para ver sus paquetes, o no tiene paquetes.",
                        columnas("Descripción", "Recibido", "Estado"), filas)));
    }

    @PostMapping("/porteria/paquetes")
    public String recibir(@RequestParam String apartamentoId, @RequestParam String descripcion,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/porteria/paquetes?apartamentoId=" + ClienteApi.codificar(apartamentoId),
                "Paquete registrado.", () -> api.post("/porteria/paquetes",
                        Map.of("apartamentoId", apartamentoId, "descripcion", limpio(descripcion))));
    }

    @PostMapping("/porteria/paquetes/entrega")
    public String entregar(@RequestParam String id, @RequestParam String apartamentoId,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/porteria/paquetes?apartamentoId=" + ClienteApi.codificar(apartamentoId),
                "Paquete entregado.", () -> api.post("/porteria/paquetes/" + id + "/entrega", Map.of()));
    }

    private static String nombreDeApartamento(List<JsonNode> apartamentos, String id) {
        for (JsonNode apartamento : apartamentos) {
            if (id.equals(t(apartamento, "id"))) {
                return nombreApartamento(apartamento);
            }
        }
        return id;
    }
}
