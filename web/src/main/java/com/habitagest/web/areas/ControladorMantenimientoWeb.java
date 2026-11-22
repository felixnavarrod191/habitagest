package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.pantallas.Opcion;
import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Área del encargado de mantenimiento (crea órdenes y ejecuta las aprobadas) y del proveedor (consulta las suyas). */
@Controller
public class ControladorMantenimientoWeb extends ControladorBase {

    private final UsuarioRepository usuarios;

    public ControladorMantenimientoWeb(ClienteApi api, UsuarioRepository usuarios) {
        super(api);
        this.usuarios = usuarios;
    }

    static Fila filaOrden(JsonNode orden) {
        String costoFinal = orden.hasNonNull("costoFinal") ? pesos(d(orden, "costoFinal")) : "";
        return fila(celdas(t(orden, "descripcion"), t(orden, "zona"), pesos(d(orden, "costoEstimado")), costoFinal,
                t(orden, "estado"), t(orden, "proveedorId"), t(orden, "aprobadaPor")));
    }

    static final List<String> COLUMNAS = columnas("Descripción", "Zona", "Costo estimado", "Costo final", "Estado",
            "Proveedor", "Aprobada por");

    @GetMapping("/mantenimiento/ordenes")
    public String ordenes(Model modelo) {
        Carga carga = carga();
        List<JsonNode> ordenes = carga.lista("/mantenimiento/ordenes");
        List<Opcion> aprobadas = new ArrayList<>();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode orden : ordenes) {
            filas.add(filaOrden(orden));
            if ("APROBADA".equals(t(orden, "estado"))) {
                aprobadas.add(new Opcion(t(orden, "id"), t(orden, "descripcion") + " · " + t(orden, "zona")));
            }
        }
        List<Opcion> proveedores = new ArrayList<>();
        proveedores.add(new Opcion("", "Sin proveedor asignado (opcional)"));
        for (Usuario usuario : usuarios.findAll()) {
            if (usuario.getRol() == Rol.PROVEEDOR && usuario.isActivo()) {
                proveedores.add(new Opcion(usuario.getUsuario(), usuario.getUsuario()));
            }
        }
        return mostrar(modelo, Rol.ENCARGADO_MANTENIMIENTO, "/mantenimiento/ordenes", "Órdenes",
                "Crea órdenes de mantenimiento y ejecuta las aprobadas con sus materiales: quedan completadas con su costo final.", carga,
                List.of(post("Crear orden de mantenimiento", "/mantenimiento/ordenes/crear", "Crear orden",
                                texto("descripcion", "Descripción"), texto("zona", "Zona"),
                                numero("costoEstimado", "Costo estimado ($)"),
                                seleccion("proveedorId", "Proveedor (opcional)", proveedores)),
                        post("Ejecutar una orden aprobada", "/mantenimiento/ordenes/ejecucion", "Ejecutar y completar",
                        seleccion("id", "Orden aprobada", aprobadas),
                        texto("descripcion1", "Material 1"), numero("valor1", "Valor del material 1 ($)"),
                        texto("descripcion2", "Material 2 (opcional)"), numero("valor2", "Valor del material 2 ($, opcional)"),
                        texto("descripcion3", "Material 3 (opcional)"), numero("valor3", "Valor del material 3 ($, opcional)"))),
                List.of(tabla("Órdenes de mantenimiento", "Todavía no hay órdenes.", COLUMNAS, filas)));
    }

    @PostMapping("/mantenimiento/ordenes/crear")
    public String crear(@RequestParam String descripcion, @RequestParam String zona,
            @RequestParam String costoEstimado, @RequestParam(defaultValue = "") String proveedorId,
            RedirectAttributes redireccion) {
        return hacer(redireccion, "/mantenimiento/ordenes", "Orden creada en estado Solicitada.", () -> {
            Map<String, Object> cuerpo = new LinkedHashMap<>();
            cuerpo.put("descripcion", limpio(descripcion));
            cuerpo.put("zona", limpio(zona));
            cuerpo.put("costoEstimado", entero(costoEstimado));
            if (!proveedorId.isBlank()) {
                cuerpo.put("proveedorId", proveedorId);
            }
            api.post("/mantenimiento/ordenes", cuerpo);
        });
    }

    @PostMapping("/mantenimiento/ordenes/ejecucion")
    public String ejecutar(@RequestParam String id, @RequestParam(defaultValue = "") String descripcion1,
            @RequestParam(defaultValue = "") String valor1, @RequestParam(defaultValue = "") String descripcion2,
            @RequestParam(defaultValue = "") String valor2, @RequestParam(defaultValue = "") String descripcion3,
            @RequestParam(defaultValue = "") String valor3, RedirectAttributes redireccion) {
        return hacer(redireccion, "/mantenimiento/ordenes", "Orden completada.", () -> {
            List<Map<String, Object>> materiales = new ArrayList<>();
            agregar(materiales, descripcion1, valor1);
            agregar(materiales, descripcion2, valor2);
            agregar(materiales, descripcion3, valor3);
            api.post("/mantenimiento/ordenes/" + id + "/ejecucion", Map.of("materiales", materiales));
        });
    }

    private static void agregar(List<Map<String, Object>> materiales, String descripcion, String valor) {
        if (descripcion.isBlank() && valor.isBlank()) {
            return;
        }
        Map<String, Object> material = new LinkedHashMap<>();
        material.put("descripcion", limpio(descripcion));
        material.put("valor", entero(valor));
        materiales.add(material);
    }

    @GetMapping("/proveedor/ordenes")
    public String ordenesDelProveedor(java.security.Principal persona, Model modelo) {
        Carga carga = carga();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode orden : carga.lista("/mantenimiento/ordenes?proveedorId="
                + ClienteApi.codificar(persona.getName()))) {
            filas.add(filaOrden(orden));
        }
        return mostrar(modelo, Rol.PROVEEDOR, "/proveedor/ordenes", "Mis órdenes",
                "Solo ves las órdenes que el administrador te asignó.", carga, List.of(),
                List.of(tabla("Órdenes asignadas a " + persona.getName(), "No tienes órdenes asignadas.",
                        COLUMNAS, filas)));
    }
}
