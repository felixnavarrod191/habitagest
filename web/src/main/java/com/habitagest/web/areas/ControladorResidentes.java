package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.pantallas.Accion;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.pantallas.Formulario;
import com.habitagest.web.pantallas.PantallasFinanzas;
import com.habitagest.web.pantallas.Tabla;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantallas del propietario y del residente (las dos áreas comparten casi todo). Cada persona ve únicamente el
 * apartamento que el administrador del sistema le asignó a su usuario; no puede escogerlo ni cambiarlo.
 */
@Controller
public class ControladorResidentes extends ControladorBase {

    private static final String AREAS = "{area:residente|propietario}";

    private final UsuarioRepository usuarios;

    public ControladorResidentes(ClienteApi api, UsuarioRepository usuarios) {
        super(api);
        this.usuarios = usuarios;
    }

    static Rol rolDe(String area) {
        return "propietario".equals(area) ? Rol.PROPIETARIO : Rol.RESIDENTE;
    }

    static String hoy() {
        return LocalDate.now(ZoneId.of("America/Bogota")).toString();
    }

    // ----- apartamento del usuario -----
    /** El apartamento asignado al usuario que inició sesión, o "" si todavía no tiene uno. */
    private String miApartamento(Principal persona, HttpSession sesion) {
        String asignado = usuarios.findByUsuario(persona.getName()).map(Usuario::getApartamentoId).orElse("");
        if (asignado == null || asignado.isBlank()) {
            sesion.removeAttribute(APARTAMENTO_ID);
            sesion.removeAttribute(APARTAMENTO_NOMBRE);
            return "";
        }
        if (!asignado.equals(apartamentoId(sesion))) {
            String nombre;
            try {
                nombre = nombreApartamento(api.get("/copropiedad/apartamentos/" + ClienteApi.codificar(asignado)));
            } catch (ErrorDelServicio error) {
                nombre = "tu apartamento";
            }
            sesion.setAttribute(APARTAMENTO_ID, asignado);
            sesion.setAttribute(APARTAMENTO_NOMBRE, nombre);
        }
        return asignado;
    }

    /** Si el usuario no tiene apartamento asignado, la pantalla solo lo avisa. */
    private String sinApartamento(Model modelo, Rol rol, String actual, String titulo, Carga carga) {
        return mostrar(modelo, rol, actual, titulo,
                "Tu usuario todavía no tiene un apartamento asignado. Pídele al administrador del sistema que lo asigne.",
                carga, List.of(), List.of());
    }

    private String rechazarSinApartamento(RedirectAttributes redireccion, String destino) {
        redireccion.addFlashAttribute("error", "Tu usuario todavía no tiene un apartamento asignado.");
        return "redirect:" + destino;
    }

    // ----- estado de cuenta (propietario) -----
    @GetMapping("/propietario/estado-de-cuenta")
    public String estadoDeCuenta(Model modelo, HttpSession sesion, Principal persona) {
        Rol rol = Rol.PROPIETARIO;
        String actual = "/propietario/estado-de-cuenta";
        Carga carga = carga();
        if (miApartamento(persona, sesion).isEmpty()) {
            return sinApartamento(modelo, rol, actual, "Estado de cuenta", carga);
        }
        List<JsonNode> apartamentos = carga.lista("/copropiedad/apartamentos");
        List<Tabla> tablas = new ArrayList<>();
        JsonNode estado = carga.objeto("/finanzas/estado-de-cuenta/" + ClienteApi.codificar(apartamentoId(sesion)));
        if (estado != null) {
            tablas.addAll(PantallasFinanzas.estadoDeCuenta(estado, apartamentos));
        }
        return mostrar(modelo, rol, actual, "Estado de cuenta",
                "Cuotas, pagos, saldo y mora del apartamento " + apartamentoNombre(sesion) + ".", carga,
                List.of(), tablas);
    }

    // ----- reservas -----
    @GetMapping("/" + AREAS + "/reservas")
    public String reservas(@PathVariable String area, @RequestParam(required = false) String zonaId,
            @RequestParam(required = false) String fecha, Model modelo, HttpSession sesion,
            Principal persona) {
        Rol rol = rolDe(area);
        String actual = "/" + area + "/reservas";
        Carga carga = carga();
        if (miApartamento(persona, sesion).isEmpty()) {
            return sinApartamento(modelo, rol, actual, "Reservas", carga);
        }
        List<JsonNode> zonas = carga.lista("/reservas/zonas");
        List<com.habitagest.web.pantallas.Opcion> opcionesZonas = new ArrayList<>();
        zonas.forEach(zona -> opcionesZonas.add(new com.habitagest.web.pantallas.Opcion(t(zona, "id"),
                t(zona, "nombre") + " (" + t(zona, "horaInicio") + " a " + t(zona, "horaFin") + ")")));

        List<Tabla> tablas = new ArrayList<>();
        if (zonaId != null && !zonaId.isBlank() && fecha != null && !fecha.isBlank()) {
            List<Fila> horarios = new ArrayList<>();
            for (JsonNode horario : carga.lista("/reservas/zonas/" + ClienteApi.codificar(zonaId)
                    + "/disponibilidad?fecha=" + ClienteApi.codificar(fecha))) {
                horarios.add(fila(celdas(fechaHora(t(horario, "inicio")), fechaHora(t(horario, "fin")),
                        "LIBRE".equals(t(horario, "estado")) ? "Libre" : "Ocupado")));
            }
            tablas.add(tabla("Disponibilidad del " + fecha, "Esa zona no tiene horarios para ese día.",
                    columnas("Desde", "Hasta", "Estado"), horarios));
        }
        List<Fila> mias = new ArrayList<>();
        for (JsonNode reserva : carga.lista("/reservas/reservas?apartamentoId="
                + ClienteApi.codificar(apartamentoId(sesion)))) {
            boolean confirmada = "CONFIRMADA".equals(t(reserva, "estado"));
            mias.add(fila(celdas(nombreZona(zonas, t(reserva, "zonaId")), fechaHora(t(reserva, "inicio")),
                    fechaHora(t(reserva, "fin")), "CONFIRMADA".equals(t(reserva, "estado")) ? "Confirmada" : "Cancelada"),
                    confirmada ? new Accion[] {accion("/" + area + "/reservas/cancelar", "Cancelar", "reservaId",
                            t(reserva, "id"))} : new Accion[0]));
        }
        tablas.add(tabla("Mis reservas", "Todavía no tienes reservas.",
                columnas("Zona", "Inicio", "Fin", "Estado"), mias));

        return mostrar(modelo, rol, actual, "Reservas",
                "Apartamento " + apartamentoNombre(sesion) + ". Máximo 2 reservas al mes, de hasta 4 horas; se cancelan hasta 24 horas antes del inicio.", carga,
                List.of(consulta("Ver la disponibilidad de una zona", actual, "Consultar",
                                seleccion("zonaId", "Zona común", opcionesZonas), fecha("fecha", "Fecha", hoy())),
                        post("Reservar una zona", actual, "Reservar",
                                seleccion("zonaId", "Zona común", opcionesZonas), fecha("fecha", "Fecha", hoy()),
                                hora("horaInicio", "Desde", "10:00"), hora("horaFin", "Hasta", "12:00"))),
                tablas);
    }

    @PostMapping("/" + AREAS + "/reservas")
    public String reservar(@PathVariable String area, @RequestParam String zonaId, @RequestParam String fecha,
            @RequestParam String horaInicio, @RequestParam String horaFin, HttpSession sesion,
            Principal persona, RedirectAttributes redireccion) {
        String apartamento = miApartamento(persona, sesion);
        if (apartamento.isEmpty()) {
            return rechazarSinApartamento(redireccion, "/" + area + "/reservas");
        }
        return hacer(redireccion, "/" + area + "/reservas", "Reserva confirmada.", () -> {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("zonaId", zonaId);
            cuerpo.put("apartamentoId", apartamento);
            cuerpo.put("inicio", fecha + "T" + horaInicio);
            cuerpo.put("fin", fecha + "T" + horaFin);
            api.post("/reservas/reservas", cuerpo);
        });
    }

    @PostMapping("/" + AREAS + "/reservas/cancelar")
    public String cancelarReserva(@PathVariable String area, @RequestParam String reservaId,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/" + area + "/reservas", "Reserva cancelada.",
                () -> api.post("/reservas/reservas/" + reservaId + "/cancelaciones", Map.of("solicitante", "RESIDENTE")));
    }

    // ----- visitas (residente) -----
    @GetMapping("/residente/visitas")
    public String visitas(@RequestParam(required = false) String fecha, Model modelo, HttpSession sesion,
            Principal persona) {
        Rol rol = Rol.RESIDENTE;
        String actual = "/residente/visitas";
        Carga carga = carga();
        if (miApartamento(persona, sesion).isEmpty()) {
            return sinApartamento(modelo, rol, actual, "Visitas", carga);
        }
        String dia = fecha == null || fecha.isBlank() ? hoy() : fecha;
        List<Fila> filas = new ArrayList<>();
        for (JsonNode visita : carga.lista("/porteria/visitas?fecha=" + ClienteApi.codificar(dia))) {
            if (apartamentoId(sesion).equals(t(visita, "apartamentoId"))) {
                filas.add(fila(celdas(t(visita, "nombre"), t(visita, "documento"), t(visita, "fecha"),
                        estadoVisita(t(visita, "estado")))));
            }
        }
        return mostrar(modelo, rol, actual, "Visitas",
                "Autoriza la visita de una persona; el portero registra su entrada y su salida.", carga,
                List.of(post("Autorizar una visita", actual, "Autorizar", texto("nombre", "Nombre del visitante"),
                                texto("documento", "Documento"), fecha("fecha", "Fecha de la visita", hoy())),
                        consulta("Ver las visitas de un día", actual, "Ver", fecha("fecha", "Día", dia))),
                List.of(tabla("Visitas del " + dia, "No hay visitas autorizadas ese día.",
                        columnas("Nombre", "Documento", "Fecha", "Estado"), filas)));
    }

    @PostMapping("/residente/visitas")
    public String autorizarVisita(@RequestParam String nombre, @RequestParam String documento,
            @RequestParam String fecha, HttpSession sesion, Principal persona, RedirectAttributes redireccion) {
        String apartamento = miApartamento(persona, sesion);
        if (apartamento.isEmpty()) {
            return rechazarSinApartamento(redireccion, "/residente/visitas");
        }
        return hacer(redireccion, "/residente/visitas", "Visita autorizada.", () -> api.post("/porteria/visitas",
                Map.of("apartamentoId", apartamento, "nombre", limpio(nombre),
                        "documento", limpio(documento), "fecha", fecha)));
    }

    static String estadoVisita(String estado) {
        return switch (estado) {
            case "AUTORIZADA" -> "Autorizada";
            case "DENTRO" -> "Dentro del conjunto";
            case "SALIO" -> "Ya salió";
            default -> estado;
        };
    }

    // ----- PQR -----
    @GetMapping("/" + AREAS + "/pqr")
    public String pqr(@PathVariable String area, Model modelo, Principal persona) {
        Rol rol = rolDe(area);
        Carga carga = carga();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode pqr : carga.lista("/pqr/pqr?residenteId=" + ClienteApi.codificar(persona.getName()))) {
            filas.add(fila(celdas(t(pqr, "numero"), t(pqr, "asunto"), t(pqr, "descripcion"), t(pqr, "estado"), fechaHora(t(pqr, "fecha")),
                    ControladorAdministracion.historial(pqr), t(pqr, "respuesta"))));
        }
        return mostrar(modelo, rol, "/" + area + "/pqr", "PQR",
                "Radica una petición, queja o reclamo y sigue su estado hasta que se cierre.", carga,
                List.of(post("Radicar una PQR", "/" + area + "/pqr", "Radicar", texto("asunto", "Asunto"),
                        area("descripcion", "Descripción"))),
                List.of(tabla("Mis PQR", "Todavía no has radicado PQR.",
                        columnas("Número", "Asunto", "Descripción", "Estado", "Fecha", "Historial", "Respuesta"), filas)));
    }

    @PostMapping("/" + AREAS + "/pqr")
    public String radicar(@PathVariable String area, @RequestParam String asunto, @RequestParam String descripcion,
            Principal persona, RedirectAttributes redireccion) {
        return hacer(redireccion, "/" + area + "/pqr", "PQR radicada.", () -> api.post("/pqr/pqr",
                Map.of("residenteId", persona.getName(), "asunto", limpio(asunto),
                        "descripcion", limpio(descripcion))));
    }

    // ----- comunicados -----
    @GetMapping("/" + AREAS + "/comunicados")
    public String comunicados(@PathVariable String area, Model modelo) {
        Carga carga = carga();
        return mostrar(modelo, rolDe(area), "/" + area + "/comunicados", "Comunicados",
                "Comunicados vigentes del conjunto.", carga, List.of(),
                List.of(ControladorConsejo.tablaComunicados(carga.lista("/pqr/comunicados"))));
    }

    // ----- paquetes -----
    @GetMapping("/" + AREAS + "/paquetes")
    public String paquetes(@PathVariable String area, Model modelo, HttpSession sesion, Principal persona) {
        Rol rol = rolDe(area);
        String actual = "/" + area + "/paquetes";
        Carga carga = carga();
        if (miApartamento(persona, sesion).isEmpty()) {
            return sinApartamento(modelo, rol, actual, "Paquetes", carga);
        }
        List<Fila> filas = new ArrayList<>();
        for (JsonNode paquete : carga.lista("/porteria/paquetes?apartamentoId="
                + ClienteApi.codificar(apartamentoId(sesion)))) {
            filas.add(fila(celdas(t(paquete, "descripcion"), fechaHora(t(paquete, "recibido")),
                    "ENTREGADO".equals(t(paquete, "estado")) ? "Entregado" : "En portería")));
        }
        return mostrar(modelo, rol, actual, "Paquetes", "Los paquetes que la portería recibió para tu apartamento.",
                carga, List.of(),
                List.of(tabla("Paquetes de " + apartamentoNombre(sesion), "No hay paquetes.",
                        columnas("Descripción", "Recibido", "Estado"), filas)));
    }

    private static String nombreZona(List<JsonNode> zonas, String id) {
        for (JsonNode zona : zonas) {
            if (id.equals(t(zona, "id"))) {
                return t(zona, "nombre");
            }
        }
        return id;
    }
}
