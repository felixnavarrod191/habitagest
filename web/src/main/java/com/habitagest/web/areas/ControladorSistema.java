package com.habitagest.web.areas;

import static com.habitagest.web.pantallas.Pantallas.*;

import com.habitagest.web.api.ClienteApi;
import com.habitagest.web.pantallas.ControladorBase;
import com.habitagest.web.seguridad.Rol;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Área del administrador del sistema: además de los usuarios, consulta los parámetros de negocio vigentes. */
@Controller
public class ControladorSistema extends ControladorBase {

    public ControladorSistema(ClienteApi api) {
        super(api);
    }

    @GetMapping("/sistema/parametros")
    public String parametros(Model modelo) {
        return mostrar(modelo, Rol.ADMIN_SISTEMA, "/sistema/parametros", "Parámetros",
                "Valores de negocio que aplican los servicios del sistema.", null, List.of(),
                List.of(tabla("Parámetros vigentes", "", columnas("Parámetro", "Valor", "Dónde se aplica"), List.of(
                        fila(celdas("Interés de mora", "2 % mensual", "Cuotas vencidas (finanzas)")),
                        fila(celdas("Día de vencimiento de la cuota", "10 de cada mes", "Generación de cuotas")),
                        fila(celdas("Reservas por apartamento", "2 al mes", "Reservas")),
                        fila(celdas("Duración máxima de una reserva", "4 horas", "Reservas")),
                        fila(celdas("Anticipación para cancelar", "24 horas", "Reservas del residente")),
                        fila(celdas("Tope de aprobación del administrador", "$2.000.000", "Mantenimiento"))))));
    }
}
