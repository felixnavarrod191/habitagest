package com.habitagest.finanzas;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

/**
 * REQ-PH-044: el revisor fiscal deja observaciones de auditoría sobre pagos y órdenes; quedan con fecha y autor y el
 * administrador del conjunto (y el Consejo) las consultan. Solo el revisor fiscal escribe.
 */
@Service
public class ServicioObservaciones {

    static final String ROL_REVISOR_FISCAL = "REVISOR_FISCAL";
    private static final Set<String> ROLES_QUE_CONSULTAN = Set.of(ROL_REVISOR_FISCAL, "ADMIN_CONJUNTO");

    private final RepositorioObservaciones observaciones;
    private final Clock reloj;

    public ServicioObservaciones(RepositorioObservaciones observaciones, Clock reloj) {
        this.observaciones = observaciones;
        this.reloj = reloj;
    }

    public Observacion dejar(String referencia, String texto, String usuario, String rol) {
        if (rol == null || !ROL_REVISOR_FISCAL.equalsIgnoreCase(rol.trim())) {
            throw ErrorDeNegocio.sinPermiso("Solo el revisor fiscal puede dejar observaciones de auditoría.");
        }
        String sobre = limpio(referencia);
        String cuerpo = limpio(texto);
        if (sobre.isEmpty() || cuerpo.isEmpty()) {
            throw ErrorDeNegocio.solicitudInvalida("La observación necesita la referencia y el texto.");
        }
        if (usuario == null || usuario.isBlank()) {
            throw ErrorDeNegocio.solicitudInvalida("Falta el usuario que deja la observación.");
        }
        return observaciones.save(new Observacion(sobre, cuerpo, usuario.trim(), LocalDateTime.now(reloj)));
    }

    /** Las observaciones, de la más reciente a la más antigua; solo para los roles que las consultan. */
    public List<Observacion> listar(String rol) {
        if (rol == null || !ROLES_QUE_CONSULTAN.contains(rol.trim().toUpperCase())) {
            throw ErrorDeNegocio.sinPermiso("Tu rol no puede consultar las observaciones de auditoría.");
        }
        return observaciones.findAll().stream()
                .sorted(Comparator.comparing(Observacion::getFecha).reversed())
                .toList();
    }

    private static String limpio(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
