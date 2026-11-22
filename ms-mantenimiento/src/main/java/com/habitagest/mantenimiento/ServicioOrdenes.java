package com.habitagest.mantenimiento;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-038 a REQ-PH-042: crear, aprobar, ejecutar y consultar las órdenes de mantenimiento. */
@Service
public class ServicioOrdenes {

    /** Parámetro del negocio: una orden con costo estimado mayor a este tope la aprueba solo el Consejo (RN-09). */
    static final long TOPE_APROBACION = 2_000_000L;

    private final RepositorioOrdenes ordenes;
    private final ServicioAuditoria auditoria;
    private final Clock reloj;

    public ServicioOrdenes(RepositorioOrdenes ordenes, ServicioAuditoria auditoria, Clock reloj) {
        this.ordenes = ordenes;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** REQ-PH-038: la orden se crea en estado Solicitada. */
    public Orden crear(String descripcion, String zona, Long costoEstimado, String proveedorId, String usuario) {
        if (vacio(descripcion) || vacio(zona)) {
            throw ErrorDeNegocio.solicitudInvalida("La descripción y la zona son obligatorias.");
        }
        if (costoEstimado == null || costoEstimado <= 0) {
            throw ErrorDeNegocio.solicitudInvalida("El costo estimado debe ser mayor que cero.");
        }
        Orden orden = ordenes.save(new Orden(descripcion.trim(), zona.trim(), costoEstimado,
                vacio(proveedorId) ? null : proveedorId.trim(), LocalDateTime.now(reloj)));
        auditoria.registrar("Orden", orden.getId(), "NINGUNO", Orden.SOLICITADA, quien(usuario));
        return orden;
    }

    /** REQ-PH-039 y REQ-PH-040: bajo el tope aprueba el administrador o el Consejo; sobre el tope, solo el Consejo. */
    public Orden aprobar(String id, AprobadoPor aprobadoPor, String usuario) {
        Orden orden = consultar(id);
        if (aprobadoPor == null) {
            throw ErrorDeNegocio.solicitudInvalida("Debe indicar quién aprueba la orden.");
        }
        if (!Orden.SOLICITADA.equals(orden.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("La orden está " + orden.getEstado() + " y solo se aprueba una orden Solicitada.");
        }
        if (orden.getCostoEstimado() > TOPE_APROBACION && aprobadoPor != AprobadoPor.CONSEJO) {
            throw ErrorDeNegocio.sinPermiso("El costo estimado supera el tope de $2.000.000: la orden la aprueba solo el Consejo.");
        }
        orden.setAprobadaPor(aprobadoPor.name());
        return mover(orden, Orden.APROBADA, usuario);
    }

    /** REQ-PH-041: ejecutar una orden Aprobada con sus materiales la deja Completada con su costo final. */
    public Orden ejecutar(String id, List<Material> materiales, String usuario) {
        Orden orden = consultar(id);
        if (materiales == null || materiales.isEmpty()) {
            throw ErrorDeNegocio.solicitudInvalida("Debe registrar al menos un material.");
        }
        if (materiales.stream().anyMatch(m -> vacio(m.getDescripcion()) || m.getValor() <= 0)) {
            throw ErrorDeNegocio.solicitudInvalida("Cada material necesita descripción y un valor mayor que cero.");
        }
        if (!Orden.APROBADA.equals(orden.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("La orden está " + orden.getEstado() + " y solo se ejecuta una orden Aprobada.");
        }
        orden.getMateriales().addAll(materiales);
        orden.setCostoFinal(materiales.stream().mapToLong(Material::getValor).sum());
        mover(orden, Orden.EN_EJECUCION, usuario);
        orden.setCompletada(LocalDateTime.now(reloj));
        return mover(orden, Orden.COMPLETADA, usuario);
    }

    public Orden consultar(String id) {
        return ordenes.findById(id).orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe la orden " + id + "."));
    }

    /** REQ-PH-042: con proveedorId, solo las órdenes que le asignaron; con estado, solo las de ese estado. */
    public List<Orden> listar(String estado, String proveedorId) {
        return ordenes.findAll().stream()
                .filter(orden -> vacio(estado) || estado.trim().equals(orden.getEstado()))
                .filter(orden -> vacio(proveedorId) || proveedorId.trim().equals(orden.getProveedorId()))
                .sorted(Comparator.comparing(Orden::getCreada).reversed())
                .toList();
    }

    /** REQ-PH-030: los cambios de estado de la orden con usuario y fecha-hora. */
    public List<Cambio> auditoria(String id) {
        return consultar(id).getHistorial();
    }

    /** Total de gastos del mes (formato AAAA-MM): suma del costo final de las órdenes completadas en ese mes. */
    public GastosMes gastos(String mes) {
        YearMonth periodo;
        try {
            periodo = YearMonth.parse(mes == null ? "" : mes.trim());
        } catch (DateTimeParseException error) {
            throw ErrorDeNegocio.solicitudInvalida("El mes debe tener el formato AAAA-MM.");
        }
        long total = ordenes.findAll().stream()
                .filter(orden -> Orden.COMPLETADA.equals(orden.getEstado()) && orden.getCompletada() != null
                        && YearMonth.from(orden.getCompletada()).equals(periodo))
                .mapToLong(orden -> orden.getCostoFinal() == null ? 0L : orden.getCostoFinal())
                .sum();
        return new GastosMes(periodo.toString(), total);
    }

    private Orden mover(Orden orden, String nuevoEstado, String usuario) {
        String anterior = orden.getEstado();
        String quien = quien(usuario);
        orden.setEstado(nuevoEstado);
        orden.getHistorial().add(new Cambio(anterior, nuevoEstado, quien, LocalDateTime.now(reloj)));
        Orden guardada = ordenes.save(orden);
        auditoria.registrar("Orden", guardada.getId(), anterior, nuevoEstado, quien);
        return guardada;
    }

    private static String quien(String usuario) {
        return vacio(usuario) ? "sistema" : usuario.trim();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
