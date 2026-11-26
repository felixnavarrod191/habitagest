package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.accion;
import static com.habitagest.web.pantallas.Pantallas.area;
import static com.habitagest.web.pantallas.Pantallas.celdas;
import static com.habitagest.web.pantallas.Pantallas.columnas;
import static com.habitagest.web.pantallas.Pantallas.d;
import static com.habitagest.web.pantallas.Pantallas.entero;
import static com.habitagest.web.pantallas.Pantallas.fecha;
import static com.habitagest.web.pantallas.Pantallas.fechaHora;
import static com.habitagest.web.pantallas.Pantallas.fila;
import static com.habitagest.web.pantallas.Pantallas.hora;
import static com.habitagest.web.pantallas.Pantallas.limpio;
import static com.habitagest.web.pantallas.Pantallas.materiales;
import static com.habitagest.web.pantallas.Pantallas.mes;
import static com.habitagest.web.pantallas.Pantallas.nombreApartamento;
import static com.habitagest.web.pantallas.Pantallas.nombreTorre;
import static com.habitagest.web.pantallas.Pantallas.numero;
import static com.habitagest.web.pantallas.Pantallas.opcionesDeApartamentos;
import static com.habitagest.web.pantallas.Pantallas.pesos;
import static com.habitagest.web.pantallas.Pantallas.post;
import static com.habitagest.web.pantallas.Pantallas.seleccion;
import static com.habitagest.web.pantallas.Pantallas.t;
import static com.habitagest.web.pantallas.Pantallas.tabla;
import static com.habitagest.web.pantallas.Pantallas.texto;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Accion;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.pantallas.Opcion;
import com.habitagest.web.pantallas.PantallasFinanzas;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Área del administrador del conjunto: unidades, cuotas, reservas, PQR, comunicados, mantenimiento y novedades. */
@Controller
public class ControladorAdministracion extends ControladorBase {

    private static final Rol ROL = Rol.ADMIN_CONJUNTO;
    private static final long TOPE_DE_APROBACION = 2_000_000L;

    private final UsuarioRepository usuarios;

    public ControladorAdministracion(ClienteApi api, UsuarioRepository usuarios) {
        super(api);
        this.usuarios = usuarios;
    }

    // ================= Unidades (HU-03 a HU-05) =================
    @GetMapping("/administracion/copropiedad")
    public String copropiedad(Model modelo) {
        Carga carga = carga();
        List<JsonNode> torres = carga.lista("/copropiedad/torres");
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<Opcion> opcionesTorres = new ArrayList<>();
        torres.forEach(torre -> opcionesTorres.add(new Opcion(t(torre, "nombre"), nombreTorre(t(torre, "nombre")))));
        List<Fila> filas = new ArrayList<>();
        double suma = 0;
        for (JsonNode apartamento : apartamentos) {
            suma += d(apartamento, "coeficiente");
            filas.add(fila(celdas(t(apartamento, "torre"), t(apartamento, "numero"),
                    String.format("%.2f %%", d(apartamento, "coeficiente")))));
        }
        List<Fila> habitantes = new ArrayList<>();
        for (Usuario persona : usuarios.findAll()) {
            if (persona.getApartamentoId() != null && !persona.getApartamentoId().isBlank()) {
                String nombre = persona.getNombreCompleto() == null || persona.getNombreCompleto().isBlank()
                        ? persona.getUsuario() : persona.getNombreCompleto();
                habitantes.add(fila(celdas(nombre, persona.getDocumento() == null ? "" : persona.getDocumento(),
                        persona.getRol().etiqueta(), persona.getUsuario(),
                        persona.getApartamentoNombre() == null ? "" : persona.getApartamentoNombre())));
            }
        }
        return mostrar(modelo, ROL, "/administracion/copropiedad", "Unidades",
                "Torres y apartamentos con su coeficiente, y quién vive en cada uno. Los propietarios y residentes se registran al crear su usuario.", carga,
                List.of(post("Crear torre", "/administracion/copropiedad/torres", "Crear torre",
                                texto("nombre", "Nombre de la torre")),
                        post("Crear apartamento", "/administracion/copropiedad/apartamentos", "Crear apartamento",
                                seleccion("torre", "Torre", opcionesTorres), texto("numero", "Número"),
                                numero("coeficiente", "Coeficiente (%)"))),
                List.of(tabla("Apartamentos (suma de coeficientes " + String.format("%.2f", suma) + " % de 100 %)",
                                "Todavía no hay apartamentos.", columnas("Torre", "Número", "Coeficiente"), filas),
                        tabla("Propietarios y residentes", "Todavía no hay propietarios ni residentes con apartamento.",
                                columnas("Nombre", "Documento", "Rol", "Usuario", "Apartamento"), habitantes)));
    }

    @PostMapping("/administracion/copropiedad/torres")
    public String crearTorre(@RequestParam String nombre, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/copropiedad", "Torre creada.",
                () -> api.post("/copropiedad/torres", Map.of("nombre", limpio(nombre))));
    }

    @PostMapping("/administracion/copropiedad/apartamentos")
    public String crearApartamento(@RequestParam String torre, @RequestParam String numero,
            @RequestParam String coeficiente, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/copropiedad", "Apartamento creado.",
                () -> api.post("/copropiedad/apartamentos", Map.of("torre", limpio(torre), "numero", limpio(numero),
                        "coeficiente", numero(coeficiente))));
    }

    // ================= Cuotas y cartera (HU-06 a HU-12) =================
    @GetMapping("/administracion/finanzas")
    public String finanzas(Model modelo) {
        Carga carga = carga();
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<JsonNode> cartera = carga.lista("/finanzas/cartera");
        List<JsonNode> cuotas = carga.lista("/finanzas/cuotas");
        return mostrar(modelo, ROL, "/administracion/finanzas", "Cuotas y cartera",
                "Presupuesto mensual, cuotas por coeficiente, mora y cartera.", carga,
                List.of(post("Guardar presupuesto del mes", "/administracion/finanzas/presupuestos", "Guardar",
                                mes("mes", "Mes"), numero("valor", "Valor del presupuesto ($)")),
                        post("Generar las cuotas del mes", "/administracion/finanzas/cuotas", "Generar cuotas",
                                mes("mes", "Mes")),
                        post("Aplicar la mora a las cuotas vencidas", "/administracion/finanzas/mora", "Aplicar mora")),
                List.of(PantallasFinanzas.cartera(cartera),
                        PantallasFinanzas.cuotas("Cuotas generadas", cuotas, apartamentos)));
    }

    @PostMapping("/administracion/finanzas/presupuestos")
    public String presupuesto(@RequestParam String mes, @RequestParam String valor, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/finanzas", "Presupuesto guardado.",
                () -> api.post("/finanzas/presupuestos", Map.of("mes", limpio(mes), "valor", numero(valor))));
    }

    @PostMapping("/administracion/finanzas/cuotas")
    public String generarCuotas(@RequestParam String mes, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/finanzas", "Cuotas generadas.",
                () -> api.post("/finanzas/cuotas/generaciones", Map.of("mes", limpio(mes))));
    }

    @PostMapping("/administracion/finanzas/mora")
    public String aplicarMora(RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/finanzas", "Mora aplicada a las cuotas vencidas.",
                () -> api.post("/finanzas/mora/aplicaciones", Map.of()));
    }

    // ================= Reservas (HU-13 a HU-16) =================
    @GetMapping("/administracion/reservas")
    public String reservas(Model modelo) {
        Carga carga = carga();
        List<JsonNode> zonas = carga.lista("/reservas/zonas");
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<JsonNode> reservas = carga.lista("/reservas/reservas");
        List<Fila> filasZonas = new ArrayList<>();
        zonas.forEach(zona -> filasZonas.add(fila(celdas(t(zona, "nombre"), t(zona, "horaInicio"),
                t(zona, "horaFin")))));
        List<Fila> filasReservas = new ArrayList<>();
        for (JsonNode reserva : reservas) {
            boolean confirmada = "CONFIRMADA".equals(t(reserva, "estado"));
            filasReservas.add(fila(celdas(nombreDe(zonas, t(reserva, "zonaId")),
                    nombreDeApartamento(apartamentos, t(reserva, "apartamentoId")), fechaHora(t(reserva, "inicio")),
                    fechaHora(t(reserva, "fin")), t(reserva, "estado")),
                    confirmada ? new Accion[] {accion("/administracion/reservas/cancelar",
                            "Cancelar", "reservaId", t(reserva, "id"))}
                            : new Accion[0]));
        }
        return mostrar(modelo, ROL, "/administracion/reservas", "Reservas",
                "Zonas comunes con su horario y cancelación de reservas (el administrador cancela en cualquier momento).",
                carga,
                List.of(post("Crear zona común", "/administracion/reservas/zonas", "Crear zona",
                        texto("nombre", "Nombre de la zona"),
                        hora("horaInicio", "Abre", "08:00"),
                        hora("horaFin", "Cierra", "22:00"))),
                List.of(tabla("Zonas comunes", "Todavía no hay zonas.", columnas("Zona", "Abre", "Cierra"), filasZonas),
                        tabla("Reservas", "Todavía no hay reservas.",
                                columnas("Zona", "Apartamento", "Inicio", "Fin", "Estado"), filasReservas)));
    }

    @PostMapping("/administracion/reservas/zonas")
    public String crearZona(@RequestParam String nombre, @RequestParam String horaInicio,
            @RequestParam String horaFin, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/reservas", "Zona creada.",
                () -> api.post("/reservas/zonas",
                        Map.of("nombre", limpio(nombre), "horaInicio", horaInicio, "horaFin", horaFin)));
    }

    @PostMapping("/administracion/reservas/cancelar")
    public String cancelarReserva(@RequestParam String reservaId, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/reservas", "Reserva cancelada.",
                () -> api.post("/reservas/reservas/" + reservaId + "/cancelaciones",
                        Map.of("solicitante", "ADMINISTRADOR")));
    }

    // ================= PQR (HU-18 y HU-20) =================
    @GetMapping("/administracion/pqr")
    public String pqr(Model modelo) {
        Carga carga = carga();
        List<JsonNode> lista = carga.lista("/pqr/pqr");
        List<Fila> filas = new ArrayList<>();
        List<Opcion> resueltas = new ArrayList<>();
        java.util.Map<String, String> apartamentoDe = new java.util.HashMap<>();
        for (Usuario persona : usuarios.findAll()) {
            if (persona.getApartamentoNombre() != null && !persona.getApartamentoNombre().isBlank()) {
                apartamentoDe.put(persona.getUsuario(), persona.getApartamentoNombre());
            }
        }
        for (JsonNode pqr : lista) {
            String estado = t(pqr, "estado");
            String siguiente = siguienteEstado(estado);
            filas.add(fila(celdas(t(pqr, "numero"), t(pqr, "residenteId"),
                    apartamentoDe.getOrDefault(t(pqr, "residenteId"), "—"), t(pqr, "asunto"), t(pqr, "descripcion"), estado,
                    fechaHora(t(pqr, "fecha")), historial(pqr), t(pqr, "respuesta")),
                    siguiente == null ? new Accion[0]
                            : new Accion[] {accion("/administracion/pqr/estado",
                                    "Pasar a " + siguiente, "id", t(pqr, "id"), "estado", siguiente)}));
            if ("RESUELTA".equals(estado)) {
                resueltas.add(new Opcion(t(pqr, "id"), t(pqr, "numero") + " · " + t(pqr, "asunto")));
            }
        }
        return mostrar(modelo, ROL, "/administracion/pqr", "PQR",
                "Cada PQR avanza solo al estado siguiente: Radicada, En revisión, En gestión, Resuelta y Cerrada.", carga,
                List.of(post("Cerrar una PQR resuelta con su respuesta", "/administracion/pqr/cierre", "Cerrar PQR",
                        seleccion("id", "PQR resuelta", resueltas), area("respuesta", "Respuesta"))),
                List.of(tabla("Todas las PQR", "Todavía no hay PQR radicadas.",
                        columnas("Número", "Residente", "Apartamento", "Asunto", "Descripción", "Estado", "Fecha", "Historial", "Respuesta"), filas)));
    }

    @PostMapping("/administracion/pqr/estado")
    public String pasarPqr(@RequestParam String id, @RequestParam String estado, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/pqr", "La PQR pasó a " + estado + ".",
                () -> api.post("/pqr/pqr/" + id + "/estado", Map.of("estado", estado)));
    }

    @PostMapping("/administracion/pqr/cierre")
    public String cerrarPqr(@RequestParam String id, @RequestParam String respuesta, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/pqr", "PQR cerrada.",
                () -> api.post("/pqr/pqr/" + id + "/cierre", Map.of("respuesta", limpio(respuesta))));
    }

    static String siguienteEstado(String estado) {
        return switch (estado) {
            case "RADICADA" -> "EN_REVISION";
            case "EN_REVISION" -> "EN_GESTION";
            case "EN_GESTION" -> "RESUELTA";
            default -> null;
        };
    }

    static String historial(JsonNode pqr) {
        StringBuilder texto = new StringBuilder();
        for (JsonNode cambio : pqr.path("historial")) {
            if (texto.length() > 0) {
                texto.append(" → ");
            }
            texto.append(t(cambio, "estadoNuevo")).append(" (").append(t(cambio, "usuario")).append(")");
        }
        return texto.length() == 0 ? "Sin cambios" : texto.toString();
    }

    // ================= Comunicados (HU-21 y HU-22) =================
    @GetMapping("/administracion/comunicados")
    public String comunicados(Model modelo) {
        Carga carga = carga();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode comunicado : carga.lista("/pqr/comunicados")) {
            filas.add(fila(celdas(t(comunicado, "titulo"), t(comunicado, "texto"), t(comunicado, "vigenteHasta"))));
        }
        return mostrar(modelo, ROL, "/administracion/comunicados", "Comunicados",
                "Los comunicados vigentes los ven propietarios y residentes; los vencidos ya no aparecen.", carga,
                List.of(post("Publicar un comunicado", "/administracion/comunicados", "Publicar",
                        texto("titulo", "Título"), area("texto", "Texto"),
                        fecha("vigenteHasta", "Vigente hasta", java.time.LocalDate.now(java.time.ZoneId.of("America/Bogota")).plusDays(7).toString()))),
                List.of(tabla("Comunicados vigentes", "No hay comunicados vigentes.",
                        columnas("Título", "Texto", "Vigente hasta"), filas)));
    }

    @PostMapping("/administracion/comunicados")
    public String publicar(@RequestParam String titulo, @RequestParam String texto,
            @RequestParam String vigenteHasta, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/comunicados", "Comunicado publicado.",
                () -> api.post("/pqr/comunicados",
                        Map.of("titulo", limpio(titulo), "texto", limpio(texto), "vigenteHasta", vigenteHasta)));
    }

    // ================= Mantenimiento (HU-27 y HU-28) =================
    @GetMapping("/administracion/mantenimiento")
    public String mantenimiento(Model modelo) {
        Carga carga = carga();
        List<JsonNode> ordenes = carga.lista("/mantenimiento/ordenes");
        List<Opcion> proveedores = new ArrayList<>();
        proveedores.add(new Opcion("", "Sin proveedor asignado"));
        for (Usuario usuario : usuarios.findAll()) {
            if (usuario.getRol() == Rol.PROVEEDOR && usuario.isActivo()) {
                proveedores.add(new Opcion(usuario.getUsuario(), usuario.getUsuario()));
            }
        }
        List<Fila> filas = new ArrayList<>();
        for (JsonNode orden : ordenes) {
            long costo = Math.round(d(orden, "costoEstimado"));
            boolean solicitada = "SOLICITADA".equals(t(orden, "estado"));
            String detalle = costo > TOPE_DE_APROBACION ? "Requiere al Consejo" : "";
            filas.add(fila(celdas(t(orden, "descripcion"), t(orden, "zona"), pesos(costo), t(orden, "estado"),
                    t(orden, "proveedorId"), t(orden, "aprobadaPor"), materiales(orden), detalle),
                    solicitada && costo <= TOPE_DE_APROBACION
                            ? new Accion[] {accion("/administracion/mantenimiento/aprobar",
                                    "Aprobar", "id", t(orden, "id"))}
                            : new Accion[0]));
        }
        return mostrar(modelo, ROL, "/administracion/mantenimiento", "Mantenimiento",
                "El administrador aprueba las órdenes hasta $2.000.000; las que superan el tope las aprueba el Consejo.",
                carga,
                List.of(post("Crear orden de mantenimiento", "/administracion/mantenimiento/ordenes", "Crear orden",
                        texto("descripcion", "Descripción"), texto("zona", "Zona"),
                        numero("costoEstimado", "Costo estimado ($)"),
                        seleccion("proveedorId", "Proveedor (opcional)", proveedores))),
                List.of(tabla("Órdenes", "Todavía no hay órdenes.",
                        columnas("Descripción", "Zona", "Costo estimado", "Estado", "Proveedor", "Aprobada por", "Materiales", "Nota"),
                        filas)));
    }

    @PostMapping("/administracion/mantenimiento/ordenes")
    public String crearOrden(@RequestParam String descripcion, @RequestParam String zona,
            @RequestParam String costoEstimado, @RequestParam(defaultValue = "") String proveedorId,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/mantenimiento", "Orden creada en estado Solicitada.", () -> {
            Map<String, Object> cuerpo = new java.util.LinkedHashMap<>();
            cuerpo.put("descripcion", limpio(descripcion));
            cuerpo.put("zona", limpio(zona));
            cuerpo.put("costoEstimado", entero(costoEstimado));
            if (!proveedorId.isBlank()) {
                cuerpo.put("proveedorId", proveedorId);
            }
            api.post("/mantenimiento/ordenes", cuerpo);
        });
    }

    @PostMapping("/administracion/mantenimiento/aprobar")
    public String aprobarOrden(@RequestParam String id, RedirectAttributes redireccion) {
        return hacer(redireccion, "/administracion/mantenimiento", "Orden aprobada.",
                () -> api.post("/mantenimiento/ordenes/" + id + "/aprobacion", Map.of("aprobadoPor", "ADMIN_CONJUNTO")));
    }

    // ================= Novedades de seguridad (HU-26) =================
    @GetMapping("/administracion/novedades")
    public String novedades(Model modelo) {
        Carga carga = carga();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode novedad : carga.lista("/porteria/novedades")) {
            filas.add(fila(celdas(fechaHora(t(novedad, "fecha")), t(novedad, "autor"), t(novedad, "texto"))));
        }
        return mostrar(modelo, ROL, "/administracion/novedades", "Novedades de seguridad",
                "Las novedades que registran los vigilantes, con su fecha, hora y autor.", carga, List.of(),
                List.of(tabla("Novedades", "Todavía no hay novedades.", columnas("Fecha y hora", "Autor", "Novedad"),
                        filas)));
    }

    // ----- utilidades -----
    private static String nombreDe(List<JsonNode> zonas, String id) {
        for (JsonNode zona : zonas) {
            if (id.equals(t(zona, "id"))) {
                return t(zona, "nombre");
            }
        }
        return id;
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
