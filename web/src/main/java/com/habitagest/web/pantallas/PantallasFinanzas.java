package com.habitagest.web.pantallas;

import static com.habitagest.web.pantallas.Pantallas.accion;
import static com.habitagest.web.pantallas.Pantallas.celdas;
import static com.habitagest.web.pantallas.Pantallas.columnas;
import static com.habitagest.web.pantallas.Pantallas.d;
import static com.habitagest.web.pantallas.Pantallas.fila;
import static com.habitagest.web.pantallas.Pantallas.nombreApartamento;
import static com.habitagest.web.pantallas.Pantallas.pesos;
import static com.habitagest.web.pantallas.Pantallas.t;
import static com.habitagest.web.pantallas.Pantallas.tabla;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;

/** Tablas de finanzas que comparten el administrador, el contador, el revisor fiscal y el propietario. */
public final class PantallasFinanzas {

    private PantallasFinanzas() {
    }

    /** Cartera: saldo y mora por apartamento (REQ-PH-014). */
    public static Tabla cartera(List<JsonNode> items) {
        List<Fila> filas = new ArrayList<>();
        double total = 0;
        for (JsonNode item : items) {
            total += d(item, "saldo");
            filas.add(fila(celdas(t(item, "torre"), t(item, "numero"), pesos(d(item, "saldo")),
                    item.path("enMora").asBoolean(false) ? "En mora" : "Al día")));
        }
        return tabla("Cartera (total " + pesos(total) + ")", "Todavía no hay cuotas generadas.",
                columnas("Torre", "Apartamento", "Saldo", "Estado"), filas);
    }

    /** Cuotas con su valor, saldo, mora y estado; con una ruta de pago agrega el botón de registrar el saldo. */
    public static Tabla cuotas(String titulo, List<JsonNode> cuotas, List<JsonNode> apartamentos) {
        List<Fila> filas = new ArrayList<>();
        for (JsonNode cuota : cuotas) {
            filas.add(fila(celdas(nombreDe(apartamentos, t(cuota, "apartamentoId")), t(cuota, "mes"),
                    pesos(d(cuota, "valor")), pesos(d(cuota, "mora")), pesos(d(cuota, "saldo")), t(cuota, "estado"))));
        }
        return tabla(titulo, "No hay cuotas para mostrar.",
                columnas("Apartamento", "Mes", "Valor", "Mora", "Saldo", "Estado"), filas);
    }

    /** Cuotas con saldo pendiente y un botón que paga el saldo completo (REQ-PH-011). */
    public static Tabla cuotasPorPagar(String rutaPago, List<JsonNode> cuotas, List<JsonNode> apartamentos) {
        List<Fila> filas = new ArrayList<>();
        for (JsonNode cuota : cuotas) {
            if (d(cuota, "saldo") <= 0) {
                continue;
            }
            filas.add(fila(celdas(nombreDe(apartamentos, t(cuota, "apartamentoId")), t(cuota, "mes"),
                    pesos(d(cuota, "valor")), pesos(d(cuota, "mora")), pesos(d(cuota, "saldo")), t(cuota, "estado")),
                    accion(rutaPago, "Pagar el saldo", "cuotaId", t(cuota, "id"), "valor",
                            String.valueOf(d(cuota, "saldo")))));
        }
        return tabla("Cuotas con saldo pendiente", "Este apartamento no tiene cuotas pendientes.",
                columnas("Apartamento", "Mes", "Valor", "Mora", "Saldo", "Estado"), filas);
    }

    public static Tabla pagos(List<JsonNode> pagos) {
        List<Fila> filas = new ArrayList<>();
        for (JsonNode pago : pagos) {
            filas.add(fila(celdas(Pantallas.fechaHora(t(pago, "fecha")), pesos(d(pago, "valor")), t(pago, "cuotaId"))));
        }
        return tabla("Pagos registrados", "Todavía no hay pagos.", columnas("Fecha", "Valor", "Cuota"), filas);
    }

    static String nombreDe(List<JsonNode> apartamentos, String id) {
        for (JsonNode apartamento : apartamentos) {
            if (id.equals(t(apartamento, "id"))) {
                return nombreApartamento(apartamento);
            }
        }
        return id;
    }

    /** Estado de cuenta de un apartamento: resumen, cuotas y pagos (REQ-PH-015). */
    public static List<Tabla> estadoDeCuenta(JsonNode estado, List<JsonNode> apartamentos) {
        List<JsonNode> cuotas = new ArrayList<>();
        estado.path("cuotas").forEach(cuotas::add);
        List<JsonNode> pagos = new ArrayList<>();
        estado.path("pagos").forEach(pagos::add);
        Tabla resumen = tabla("Resumen", "", columnas("Saldo total", "Mora acumulada"),
                List.of(fila(celdas(pesos(d(estado, "saldo")), pesos(d(estado, "mora"))))));
        return List.of(resumen, cuotas("Cuotas", cuotas, apartamentos), pagos(pagos));
    }
}
