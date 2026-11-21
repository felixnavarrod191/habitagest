package com.habitagest.web.usuarios;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.pantallas.Opcion;
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

/** HU-01 y REQ-PH-002: el administrador del sistema crea usuarios con un rol y una contraseña cifrada. */
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

    private String nombreDelApartamento(String id) {
        for (Opcion opcion : apartamentos()) {
            if (opcion.valor().equals(id)) {
                return opcion.texto();
            }
        }
        return "";
    }

    private List<Opcion> apartamentos() {
        try {
            JsonNode lista = api.get("/copropiedad/apartamentos");
            List<JsonNode> nodos = new ArrayList<>();
            if (lista != null && lista.isArray()) {
                lista.forEach(nodos::add);
            }
            return Pantallas.opcionesDeApartamentos(nodos);
        } catch (ErrorDelServicio error) {
            return List.of();
        }
    }

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("usuarios", repositorio.findAll());
        modelo.addAttribute("roles", Rol.values());
        modelo.addAttribute("apartamentos", apartamentos());
        return "usuarios";
    }

    @PostMapping
    public String crear(@RequestParam String usuario, @RequestParam String contrasena, @RequestParam Rol rol,
            @RequestParam(defaultValue = "") String apartamentoId, RedirectAttributes avisos) {
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
        if (tieneApartamento(rol) && !apartamentoId.isBlank()) {
            nuevo.setApartamentoId(apartamentoId);
            nuevo.setApartamentoNombre(nombreDelApartamento(apartamentoId));
        }
        repositorio.save(nuevo);
        avisos.addFlashAttribute("mensaje", "Usuario creado con el rol " + rol.etiqueta() + ".");
        return LISTA;
    }

    /** Asigna (o quita) el apartamento de un propietario o residente; la persona no puede cambiarlo por su cuenta. */
    @PostMapping("/apartamento")
    public String asignarApartamento(@RequestParam String usuario,
            @RequestParam(defaultValue = "") String apartamentoId, RedirectAttributes avisos) {
        Optional<Usuario> encontrado = repositorio.findByUsuario(usuario);
        if (encontrado.isEmpty()) {
            avisos.addFlashAttribute("error", "No existe el usuario " + usuario + ".");
        } else if (!tieneApartamento(encontrado.get().getRol())) {
            avisos.addFlashAttribute("error", "Solo los usuarios propietario y residente tienen apartamento.");
        } else {
            Usuario persona = encontrado.get();
            persona.setApartamentoId(apartamentoId.isBlank() ? null : apartamentoId);
            persona.setApartamentoNombre(apartamentoId.isBlank() ? null : nombreDelApartamento(apartamentoId));
            repositorio.save(persona);
            avisos.addFlashAttribute("mensaje", apartamentoId.isBlank()
                    ? "Apartamento quitado a " + usuario + "."
                    : "Apartamento asignado a " + usuario + ".");
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
