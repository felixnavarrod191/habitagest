package com.habitagest.web.pantallas;

import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** Datos que el menú lateral necesita en todas las páginas: las opciones del rol, la ruta actual y el apartamento. */
@ControllerAdvice
public class DatosDePagina {

    private final UsuarioRepository usuarios;

    public DatosDePagina(UsuarioRepository usuarios) {
        this.usuarios = usuarios;
    }

    @ModelAttribute("rutaActual")
    public String rutaActual(HttpServletRequest peticion) {
        return peticion.getRequestURI();
    }

    @ModelAttribute("menuLateral")
    public List<Enlace> menuLateral(Principal persona) {
        Rol rol = rolDe(persona);
        return rol == null ? List.of() : Areas.menu(rol);
    }

    /** El apartamento asignado, solo para propietarios y residentes; vacío en los demás casos. */
    @ModelAttribute("miApartamento")
    public String miApartamento(Principal persona) {
        Rol rol = rolDe(persona);
        if (rol != Rol.RESIDENTE && rol != Rol.PROPIETARIO) {
            return "";
        }
        String nombre = usuarios.findByUsuario(persona.getName()).map(Usuario::getApartamentoNombre).orElse("");
        return nombre == null ? "" : nombre;
    }

    private static Rol rolDe(Principal persona) {
        if (!(persona instanceof Authentication sesion) || sesion instanceof AnonymousAuthenticationToken) {
            return null;
        }
        for (GrantedAuthority autoridad : sesion.getAuthorities()) {
            try {
                return Rol.valueOf(autoridad.getAuthority().replaceFirst("^ROLE_", ""));
            } catch (IllegalArgumentException ignorada) {
                // no es uno de los 11 roles: se prueba con la siguiente autoridad
            }
        }
        return null;
    }
}
