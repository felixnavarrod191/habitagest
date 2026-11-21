package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Accion;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.pantallas.PantallasFinanzas;
import com.habitagest.web.pantallas.Tabla;
import com.habitagest.web.seguridad.Rol;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Área del Consejo de administración: aprueba las órdenes que superan el tope y consulta el resumen. */
@Controller
public class ControladorConsejo extends ControladorBase {

    private static final Rol ROL = Rol.CONSEJO;
    private static final long TOPE = 2_000_000L;

    public ControladorConsejo(ClienteApi api) {
        super(api);
    }

    static Tabla tablaComunicados(List<JsonNode> comunicados) {
        List<Fila> filas = new ArrayList<>();
        for (JsonNode comunicado : comunicados) {
            filas.add(fila(celdas(t(comunicado, "titulo"), t(comunicado, "texto"), t(comunicado, "vigenteHasta"))));
        }
        return tabla("Comunicados vigentes", "No hay comunicados vigentes.",
                columnas("Título", "Texto", "Vigente hasta"), filas);
    }

    @GetMapping("/consejo/ordenes")
    public String ordenes(Model modelo) {
        Carga carga = carga();
        List<Fila> pendientes = new ArrayList<>();
        for (JsonNode orden : carga.lista("/mantenimiento/ordenes?estado=SOLICITADA")) {
            long costo = Math.round(d(orden, "costoEstimado"));
            pendientes.add(fila(celdas(t(orden, "descripcion"), t(orden, "zona"), pesos(costo),
                    costo > TOPE ? "Supera el tope: solo el Consejo" : "Dentro del tope", t(orden, "proveedorId")),
                    accion("/consejo/ordenes/aprobar", "Aprobar", "id", t(orden, "id"))));
        }
        List<Fila> todas = new ArrayList<>();
        for (JsonNode orden : carga.lista("/mantenimiento/ordenes")) {
            todas.add(fila(celdas(t(orden, "descripcion"), pesos(d(orden, "costoEstimado")), t(orden, "estado"),
                    t(orden, "aprobadaPor"))));
        }
        return mostrar(modelo, ROL, "/consejo/ordenes", "Aprobar órdenes",
                "El Consejo aprueba las órdenes de mantenimiento, en especial las de más de $2.000.000.", carga,
                List.of(),
                List.of(tabla("Órdenes por aprobar", "No hay órdenes por aprobar.",
                                columnas("Descripción", "Zona", "Costo estimado", "Aprobación", "Proveedor"), pendientes),
                        tabla("Todas las órdenes", "Todavía no hay órdenes.",
                                columnas("Descripción", "Costo estimado", "Estado", "Aprobada por"), todas)));
    }

    @PostMapping("/consejo/ordenes/aprobar")
    public String aprobar(@RequestParam String id, RedirectAttributes redireccion) {
        return hacer(redireccion, "/consejo/ordenes", "Orden aprobada por el Consejo.",
                () -> api.post("/mantenimiento/ordenes/" + id + "/aprobacion", Map.of("aprobadoPor", "CONSEJO")));
    }

    @GetMapping("/consejo/resumen")
    public String resumen(@RequestParam(required = false) String mes, Model modelo) {
        Carga carga = carga();
        String periodo = mes == null || mes.isBlank()
                ? YearMonth.now(ZoneId.of("America/Bogota")).toString() : mes.trim();
        List<Fila> gastos = new ArrayList<>();
        JsonNode total = carga.objeto("/mantenimiento/gastos?mes=" + ClienteApi.codificar(periodo));
        if (total != null) {
            gastos.add(fila(celdas(t(total, "mes"), pesos(d(total, "total")))));
        }
        return mostrar(modelo, ROL, "/consejo/resumen", "Resumen",
                "Cartera por apartamento y gastos de mantenimiento del mes.", carga,
                List.of(consulta("Gastos de otro mes", "/consejo/resumen", "Consultar",
                        mes("mes", "Mes"))),
                List.of(PantallasFinanzas.cartera(carga.lista("/finanzas/cartera")),
                        tabla("Gastos de mantenimiento completados", "Sin gastos.",
                                columnas("Mes", "Total"), gastos)));
    }

    @GetMapping("/consejo/comunicados")
    public String comunicados(Model modelo) {
        Carga carga = carga();
        return mostrar(modelo, ROL, "/consejo/comunicados", "Comunicados", "Comunicados vigentes del conjunto.",
                carga, List.of(), List.of(tablaComunicados(carga.lista("/pqr/comunicados"))));
    }
}
