package com.habitagest.web.usuarios;

import com.habitagest.web.seguridad.Rol;
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

    public ControladorUsuarios(UsuarioRepository repositorio, PasswordEncoder codificador) {
        this.repositorio = repositorio;
        this.codificador = codificador;
    }

    @GetMapping
    public String listar(Model modelo) {
        modelo.addAttribute("usuarios", repositorio.findAll());
        modelo.addAttribute("roles", Rol.values());
        return "usuarios";
    }

    @PostMapping
    public String crear(@RequestParam String usuario, @RequestParam String contrasena, @RequestParam Rol rol,
            RedirectAttributes avisos) {
        String nombre = usuario.trim();
        if (nombre.isEmpty() || contrasena.length() < 8) {
            avisos.addFlashAttribute("error", "El usuario es obligatorio y la contraseña debe tener al menos 8 caracteres.");
            return LISTA;
        }
        if (repositorio.existsByUsuario(nombre)) {
            avisos.addFlashAttribute("error", "Ya existe un usuario con ese nombre.");
            return LISTA;
        }
        repositorio.save(new Usuario(nombre, codificador.encode(contrasena), rol, true));
        avisos.addFlashAttribute("mensaje", "Usuario creado con el rol " + rol.etiqueta() + ".");
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
