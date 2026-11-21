package com.habitagest.web.pantallas;

import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.api.ErrorDelServicio;
import com.habitagest.web.seguridad.Rol;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Utilidades comunes de los controladores de pantallas por rol. */
public abstract class ControladorBase {

    protected final ClienteApi api;

    protected ControladorBase(ClienteApi api) {
        this.api = api;
    }

    protected Carga carga() {
        return new Carga(api);
    }

    /** Muestra la pantalla genérica con el menú del rol, los avisos de la carga y los formularios y tablas. */
    protected String mostrar(Model modelo, Rol rol, String actual, String titulo, String descripcion, Carga carga,
            List<Formulario> formularios, List<Tabla> tablas) {
        Pantalla pantalla = new Pantalla(titulo, descripcion, rol.etiqueta(), Areas.menu(rol), actual,
                carga == null ? List.of() : carga.avisos(), formularios, tablas);
        modelo.addAttribute("p", pantalla);
        modelo.addAttribute("rutaArea", rol.rutaBase());
        return "pantalla";
    }

    /** Ejecuta una acción sobre los servicios y vuelve a la pantalla con un mensaje de éxito o de error. */
    protected String hacer(RedirectAttributes redireccion, String destino, String exito, Operacion operacion) {
        try {
            operacion.ejecutar();
            redireccion.addFlashAttribute("mensaje", exito);
        } catch (ErrorDelServicio error) {
            redireccion.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:" + destino;
    }

    /** Lo que hace un botón o un formulario. */
    @FunctionalInterface
    protected interface Operacion {
        void ejecutar();
    }

    // ----- apartamento elegido por propietarios y residentes -----
    protected static final String APARTAMENTO_ID = "apartamentoId";
    protected static final String APARTAMENTO_NOMBRE = "apartamentoNombre";

    protected static String apartamentoId(HttpSession sesion) {
        Object valor = sesion.getAttribute(APARTAMENTO_ID);
        return valor == null ? "" : valor.toString();
    }

    protected static String apartamentoNombre(HttpSession sesion) {
        Object valor = sesion.getAttribute(APARTAMENTO_NOMBRE);
        return valor == null ? "" : valor.toString();
    }
}
