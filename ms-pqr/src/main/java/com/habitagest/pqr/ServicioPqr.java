package com.habitagest.pqr;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/** REQ-PH-025 a REQ-PH-030: radicar, gestionar en orden, consultar, cerrar y auditar las PQR. */
@Service
public class ServicioPqr {

    /** RN-08: cada estado solo puede pasar al siguiente. */
    private static final Map<String, String> SIGUIENTE = Map.of(
            Pqr.RADICADA, Pqr.EN_REVISION,
            Pqr.EN_REVISION, Pqr.EN_GESTION,
            Pqr.EN_GESTION, Pqr.RESUELTA);

    private final RepositorioPqr pqrs;
    private final ServicioAuditoria auditoria;
    private final Clock reloj;

    public ServicioPqr(RepositorioPqr pqrs, ServicioAuditoria auditoria, Clock reloj) {
        this.pqrs = pqrs;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** REQ-PH-025: la PQR queda Radicada con su número, su fecha y su autor. */
    public Pqr radicar(String residenteId, String asunto, String descripcion, String usuario) {
        if (vacio(residenteId) || vacio(asunto) || vacio(descripcion)) {
            throw ErrorDeNegocio.solicitudInvalida("El residente, el asunto y la descripción son obligatorios.");
        }
        String numero = String.format("PQR-%04d", pqrs.count() + 1);
        Pqr pqr = pqrs.save(new Pqr(numero, residenteId.trim(), asunto.trim(), descripcion.trim(),
                LocalDateTime.now(reloj)));
        auditoria.registrar("PQR", pqr.getId(), "NINGUNO", Pqr.RADICADA, quien(usuario, residenteId));
        return pqr;
    }

    /** REQ-PH-028: sin filtro devuelve todas (administrador); con residenteId, solo las de ese residente. */
    public List<Pqr> listar(String residenteId) {
        List<Pqr> lista = vacio(residenteId) ? pqrs.findAll() : pqrs.findByResidenteId(residenteId.trim());
        return lista.stream().sorted(Comparator.comparing(Pqr::getFecha).reversed()).toList();
    }

    public Pqr consultar(String id) {
        return pqrs.findById(id).orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe la PQR " + id + "."));
    }

    /** REQ-PH-026 y REQ-PH-027: solo al estado siguiente; cualquier salto se rechaza y la PQR no cambia. */
    public Pqr cambiarEstado(String id, String nuevoEstado, String usuario) {
        Pqr pqr = consultar(id);
        if (nuevoEstado == null || !List.of(Pqr.EN_REVISION, Pqr.EN_GESTION, Pqr.RESUELTA).contains(nuevoEstado)) {
            throw ErrorDeNegocio.solicitudInvalida("El estado debe ser EN_REVISION, EN_GESTION o RESUELTA.");
        }
        if (Pqr.CERRADA.equals(pqr.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("La PQR está cerrada y no se puede modificar.");
        }
        String siguiente = SIGUIENTE.get(pqr.getEstado());
        if (!nuevoEstado.equals(siguiente)) {
            throw ErrorDeNegocio.reglaDeNegocio("La PQR está " + pqr.getEstado() + " y solo puede pasar a "
                    + siguiente + ".");
        }
        return mover(pqr, nuevoEstado, usuario);
    }

    /** REQ-PH-029: solo una PQR Resuelta se cierra, con su respuesta; después no se modifica. */
    public Pqr cerrar(String id, String respuesta, String usuario) {
        Pqr pqr = consultar(id);
        if (vacio(respuesta)) {
            throw ErrorDeNegocio.solicitudInvalida("La respuesta es obligatoria para cerrar la PQR.");
        }
        if (Pqr.CERRADA.equals(pqr.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("La PQR está cerrada y no se puede modificar.");
        }
        if (!Pqr.RESUELTA.equals(pqr.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("Solo se puede cerrar una PQR Resuelta.");
        }
        pqr.setRespuesta(respuesta.trim());
        return mover(pqr, Pqr.CERRADA, usuario);
    }

    /** REQ-PH-030: los cambios de estado con usuario y fecha-hora. */
    public List<Cambio> auditoria(String id) {
        return consultar(id).getHistorial();
    }

    private Pqr mover(Pqr pqr, String nuevoEstado, String usuario) {
        String anterior = pqr.getEstado();
        String quien = quien(usuario, "sistema");
        pqr.setEstado(nuevoEstado);
        pqr.getHistorial().add(new Cambio(anterior, nuevoEstado, quien, LocalDateTime.now(reloj)));
        Pqr guardada = pqrs.save(pqr);
        auditoria.registrar("PQR", guardada.getId(), anterior, nuevoEstado, quien);
        return guardada;
    }

    private static String quien(String usuario, String porDefecto) {
        return vacio(usuario) ? porDefecto : usuario.trim();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
