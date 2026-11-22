package com.habitagest.web.usuarios;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.pantallas.Pantallas;
import com.habitagest.web.seguridad.Rol;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * HU-01 y REQ-PH-002: el administrador del sistema crea usuarios con un rol y una contraseña cifrada.
 * Un propietario o residente se crea con su nombre, su documento, su torre y su apartamento: esos datos
 * quedan en el usuario y se registran en ms-copropiedad (que aplica la regla de un solo propietario por apartamento).
 */
@Controller
@RequestMapping("/sistema/usuarios")
public class ControladorUsuarios {

    private static final String LISTA = "redirect:/sistema/usuarios";

    private final UsuarioRepository repositorio;
    private final PasswordEncoder codificador;
    private final ClienteApi api;

    public ControladorUsuarios(UsuarioRepository repositorio, PasswordEncoder codificador, ClienteApi api) {
        this.repositorio = repositorio;
        this.codificador = codificador;
        this.api = api;
    }

    static boolean tieneApartamento(Rol rol) {
        return rol == Rol.PROPIETARIO || rol == Rol.RESIDENTE;
    }

    /** Un apartamento para los dos selectores de la pantalla: primero la torre y luego el apartamento de esa torre. */
    public record Unidad(String id, String torre, String torreNombre, String numero, String nombre) {
    }

    private List<Unidad> unidades() {
        try {
            JsonNode lista = api.get("/copropiedad/apartamentos");
            List<Unidad> unidades = new ArrayList<>();
            if (lista != null && lista.isArray()) {
                for (JsonNode nodo : lista) {
                    String torre = nodo.path("torre").asText("");
                    unidades.add(new Unidad(nodo.path("id").asText(""), torre, Pantallas.nombreTorre(torre),
                            nodo.path("numero").asText(""), Pantallas.nombreApartamento(nodo)));
                }
            }
            return unidades;
        } catch (ErrorDelServicio error) {
            return List.of();
        }
    }

    private static List<String> torresDe(List<Unidad> unidades) {
        List<String> torres = new ArrayList<>();
        for (Unidad unidad : unidades) {
            if (!torres.contains(unidad.torre())) {
                torres.add(unidad.torre());
            }
        }
        return torres;
    }

    /**
     * Valida y registra a un propietario o residente: el apartamento debe existir, el nombre y el documento son
     * obligatorios y ms-copropiedad guarda a la persona en su apartamento. Devuelve el error, o null si todo salió bien.
     */
    private String vincular(Usuario persona, String apartamentoId, String nombre, String documento) {
        if (apartamentoId.isBlank() || nombre.isBlank() || documento.isBlank()) {
            return "Un " + persona.getRol().etiqueta().toLowerCase() + " necesita nombre completo, documento, torre y apartamento.";
        }
        String nombreApartamento = null;
        for (Unidad unidad : unidades()) {
            if (unidad.id().equals(apartamentoId)) {
                nombreApartamento = unidad.nombre();
            }
        }
        if (nombreApartamento == null) {
            return "El apartamento elegido no existe.";
        }
        String ruta = persona.getRol() == Rol.PROPIETARIO ? "/copropiedad/propietarios" : "/copropiedad/residentes";
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("apartamentoId", apartamentoId);
        cuerpo.put("nombre", nombre.trim());
        cuerpo.put("documento", documento.trim());
        try {
            api.post(ruta, cuerpo);
        } catch (ErrorDelServicio error) {
            return error.getMessage();
        }
        persona.setApartamentoId(apartamentoId);
        persona.setApartamentoNombre(nombreApartamento);
        persona.setNombreCompleto(nombre.trim());
        persona.setDocumento(documento.trim());
        return null;
    }

    @GetMapping
    public String listar(Model modelo) {
        List<Unidad> unidades = unidades();
        modelo.addAttribute("usuarios", repositorio.findAll());
        modelo.addAttribute("roles", Rol.values());
        modelo.addAttribute("unidades", unidades);
        modelo.addAttribute("torres", torresDe(unidades));
        return "usuarios";
    }

    @PostMapping
    public String crear(@RequestParam String usuario, @RequestParam String contrasena, @RequestParam Rol rol,
            @RequestParam(defaultValue = "") String apartamentoId, @RequestParam(defaultValue = "") String nombreCompleto,
            @RequestParam(defaultValue = "") String documento, RedirectAttributes avisos) {
        String nombre = usuario.trim();
        if (nombre.isEmpty() || contrasena.length() < 8) {
            avisos.addFlashAttribute("error", "El usuario es obligatorio y la contraseña debe tener al menos 8 caracteres.");
            return LISTA;
        }
        if (repositorio.existsByUsuario(nombre)) {
            avisos.addFlashAttribute("error", "Ya existe un usuario con ese nombre.");
            return LISTA;
        }
        Usuario nuevo = new Usuario(nombre, codificador.encode(contrasena), rol, true);
        if (tieneApartamento(rol)) {
            String error = vincular(nuevo, apartamentoId, nombreCompleto, documento);
            if (error != null) {
                avisos.addFlashAttribute("error", error);
                return LISTA;
            }
        }
        try {
            repositorio.save(nuevo);
        } catch (RuntimeException fallo) {
            avisos.addFlashAttribute("error", "No se pudo guardar el usuario: " + fallo.getMessage());
            return LISTA;
        }
        avisos.addFlashAttribute("mensaje", "Usuario creado con el rol " + rol.etiqueta() + ".");
        return LISTA;
    }

    /**
     * Completa a un propietario o residente que quedó sin apartamento (por ejemplo, los usuarios de demostración):
     * pide los mismos datos que al crearlo. Quien ya tiene apartamento no se cambia desde aquí.
     */
    @PostMapping("/apartamento")
    public String asignarApartamento(@RequestParam String usuario,
            @RequestParam(defaultValue = "") String apartamentoId, @RequestParam(defaultValue = "") String nombreCompleto,
            @RequestParam(defaultValue = "") String documento, RedirectAttributes avisos) {
        Optional<Usuario> encontrado = repositorio.findByUsuario(usuario);
        if (encontrado.isEmpty()) {
            avisos.addFlashAttribute("error", "No existe el usuario " + usuario + ".");
        } else if (!tieneApartamento(encontrado.get().getRol())) {
            avisos.addFlashAttribute("error", "Solo los usuarios propietario y residente tienen apartamento.");
        } else if (encontrado.get().getApartamentoId() != null && !encontrado.get().getApartamentoId().isBlank()) {
            avisos.addFlashAttribute("error", "El usuario " + usuario + " ya tiene apartamento.");
        } else {
            Usuario persona = encontrado.get();
            String error = vincular(persona, apartamentoId, nombreCompleto, documento);
            if (error != null) {
                avisos.addFlashAttribute("error", error);
            } else {
                repositorio.save(persona);
                avisos.addFlashAttribute("mensaje", "Apartamento asignado a " + usuario + ".");
            }
        }
        return LISTA;
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable String id) {
        repositorio.findById(id).ifPresent(usuario -> {
            usuario.setActivo(!usuario.isActivo());
            repositorio.save(usuario);
        });
        return LISTA;
    }
}
