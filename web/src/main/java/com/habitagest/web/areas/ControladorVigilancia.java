package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.Carga;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.pantallas.Fila;
import com.habitagest.web.seguridad.Rol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Área del vigilante: registra novedades de seguridad (REQ-PH-032). */
@Controller
public class ControladorVigilancia extends ControladorBase {

    public ControladorVigilancia(ClienteApi api) {
        super(api);
    }

    @GetMapping("/vigilancia/novedades")
    public String novedades(Model modelo) {
        Carga carga = carga();
        List<Fila> filas = new ArrayList<>();
        for (JsonNode novedad : carga.lista("/porteria/novedades")) {
            filas.add(fila(celdas(fechaHora(t(novedad, "fecha")), t(novedad, "autor"), t(novedad, "texto"))));
        }
        return mostrar(modelo, Rol.VIGILANTE, "/vigilancia/novedades", "Novedades de seguridad",
                "Cada novedad queda con la fecha, la hora y quien la registró.", carga,
                List.of(post("Registrar una novedad", "/vigilancia/novedades", "Registrar",
                        area("texto", "Qué ocurrió"))),
                List.of(tabla("Novedades registradas", "Todavía no hay novedades.",
                        columnas("Fecha y hora", "Autor", "Novedad"), filas)));
    }

    @PostMapping("/vigilancia/novedades")
    public String registrar(@RequestParam String texto, RedirectAttributes redireccion) {
        return hacer(redireccion, "/vigilancia/novedades", "Novedad registrada.",
                () -> api.post("/porteria/novedades", Map.of("texto", limpio(texto))));
    }
}
