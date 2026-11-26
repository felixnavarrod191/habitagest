package com.habitagest.web;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorInicio {

    static final String TITULO_PORTAL = "Portal web · Conjunto Residencial Torres del Mirador";

    /** Sin sesión: página de aterrizaje del conjunto con el formulario de acceso. Con sesión: las opciones del rol. */
    @GetMapping("/")
    public String inicio(Authentication autenticacion, Model modelo) {
        boolean sinSesion = autenticacion == null || autenticacion instanceof AnonymousAuthenticationToken
                || !autenticacion.isAuthenticated();
        if (sinSesion) {
            modelo.addAttribute("titulo", TITULO_PORTAL);
            return "aterrizaje";
        }
        return "inicio";
    }

    /** El acceso vive en la página de aterrizaje. */
    @GetMapping("/login")
    public String login() {
        return "redirect:/";
    }

    @GetMapping("/403")
    public String sinPermiso() {
        return "403";
    }
}
