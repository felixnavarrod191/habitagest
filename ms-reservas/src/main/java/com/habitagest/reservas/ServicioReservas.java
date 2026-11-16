package com.habitagest.reservas;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-018 a REQ-PH-024: zonas comunes, disponibilidad, reservas sin cruces y cancelaciones. */
@Service
public class ServicioReservas {

    /** Parámetros del negocio: máximo de reservas por apartamento en el mes, duración máxima y anticipación. */
    static final int MAXIMO_POR_MES = 2;
    static final Duration DURACION_MAXIMA = Duration.ofHours(4);
    static final Duration ANTICIPACION_PARA_CANCELAR = Duration.ofHours(24);

    private final RepositorioZonas zonas;
    private final RepositorioReservas reservas;
    private final ClienteFinanzas finanzas;
    private final ClienteCopropiedad copropiedad;
    private final ServicioAuditoria auditoria;
    private final Clock reloj;

    public ServicioReservas(RepositorioZonas zonas, RepositorioReservas reservas, ClienteFinanzas finanzas,
            ClienteCopropiedad copropiedad, ServicioAuditoria auditoria, Clock reloj) {
        this.zonas = zonas;
        this.reservas = reservas;
        this.finanzas = finanzas;
        this.copropiedad = copropiedad;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** REQ-PH-018: guarda la zona con su horario de uso. */
    public Zona crearZona(String nombre, String horaInicio, String horaFin) {
        String limpio = nombre == null ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            throw ErrorDeNegocio.solicitudInvalida("El nombre de la zona es obligatorio.");
        }
        LocalTime inicio = hora(horaInicio);
        LocalTime fin = hora(horaFin);
        if (!fin.isAfter(inicio)) {
            throw ErrorDeNegocio.solicitudInvalida("La hora de cierre debe ser posterior a la de apertura.");
        }
        if (zonas.existsByNombre(limpio)) {
            throw ErrorDeNegocio.reglaDeNegocio("Ya existe la zona " + limpio + ".");
        }
        return zonas.save(new Zona(limpio, inicio.toString(), fin.toString()));
    }

    public List<Zona> listarZonas() {
        return zonas.findAll();
    }

    /** REQ-PH-019: los horarios libres y ocupados de una zona en una fecha. */
    public List<Horario> disponibilidad(String zonaId, String fecha) {
        Zona zona = zonaOError(zonaId);
        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha == null ? "" : fecha.trim());
        } catch (DateTimeParseException error) {
            throw ErrorDeNegocio.solicitudInvalida("La fecha debe tener el formato AAAA-MM-DD.");
        }
        return Disponibilidad.calcular(dia, hora(zona.getHoraInicio()), hora(zona.getHoraFin()),
                reservas.findByZonaIdAndEstado(zonaId, Reserva.CONFIRMADA));
    }

    /** REQ-PH-020 a REQ-PH-022: confirma la reserva si cumple todas las reglas, o la rechaza con el motivo. */
    public Reserva reservar(String zonaId, String apartamentoId, LocalDateTime inicio, LocalDateTime fin,
            String usuario) {
        if (!fin.isAfter(inicio)) {
            throw ErrorDeNegocio.solicitudInvalida("El fin de la reserva debe ser posterior a su inicio.");
        }
        Zona zona = zonaOError(zonaId);
        if (!copropiedad.existeApartamento(apartamentoId)) {
            throw ErrorDeNegocio.noEncontrado("No existe el apartamento " + apartamentoId + ".");
        }
        LocalTime apertura = hora(zona.getHoraInicio());
        LocalTime cierre = hora(zona.getHoraFin());
        boolean mismoDia = inicio.toLocalDate().equals(fin.toLocalDate());
        if (!mismoDia || inicio.toLocalTime().isBefore(apertura) || fin.toLocalTime().isAfter(cierre)) {
            throw ErrorDeNegocio.reglaDeNegocio(
                    "La reserva debe estar dentro del horario de la zona (" + apertura + " a " + cierre + ").");
        }
        if (Duration.between(inicio, fin).compareTo(DURACION_MAXIMA) > 0) {
            throw ErrorDeNegocio.reglaDeNegocio("Una reserva dura como máximo 4 horas.");
        }
        if (finanzas.enMora(apartamentoId)) {
            throw ErrorDeNegocio.reglaDeNegocio("El apartamento está en mora y no puede reservar zonas comunes.");
        }
        YearMonth mes = YearMonth.from(inicio);
        long delMes = reservas.findByApartamentoIdAndEstado(apartamentoId, Reserva.CONFIRMADA).stream()
                .filter(reserva -> YearMonth.from(reserva.getInicio()).equals(mes)).count();
        if (delMes >= MAXIMO_POR_MES) {
            throw ErrorDeNegocio.reglaDeNegocio(
                    "El apartamento ya tiene el máximo de " + MAXIMO_POR_MES + " reservas en el mes.");
        }
        for (Reserva existente : reservas.findByZonaIdAndEstado(zonaId, Reserva.CONFIRMADA)) {
            if (existente.getInicio().isBefore(fin) && inicio.isBefore(existente.getFin())) {
                throw ErrorDeNegocio.reglaDeNegocio("El horario ya está reservado en esa zona (cruce de horario).");
            }
        }
        Reserva reserva = reservas.save(new Reserva(zonaId, apartamentoId, inicio, fin));
        auditoria.registrar("Reserva", reserva.getId(), "NINGUNO", Reserva.CONFIRMADA, usuario);
        return reserva;
    }

    /** REQ-PH-023 y REQ-PH-024: el residente cancela con 24 horas de anticipación; el administrador, siempre. */
    public Reserva cancelar(String reservaId, Solicitante solicitante, String usuario) {
        Reserva reserva = reservas.findById(reservaId)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe la reserva " + reservaId + "."));
        if (Reserva.CANCELADA.equals(reserva.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("La reserva ya está cancelada.");
        }
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (solicitante == Solicitante.RESIDENTE
                && ahora.plus(ANTICIPACION_PARA_CANCELAR).isAfter(reserva.getInicio())) {
            throw ErrorDeNegocio.reglaDeNegocio(
                    "El residente solo puede cancelar con al menos 24 horas de anticipación.");
        }
        reserva.setEstado(Reserva.CANCELADA);
        reservas.save(reserva);
        auditoria.registrar("Reserva", reserva.getId(), Reserva.CONFIRMADA, Reserva.CANCELADA, usuario);
        return reserva;
    }

    private Zona zonaOError(String zonaId) {
        return zonas.findById(zonaId).orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe la zona " + zonaId + "."));
    }

    static LocalTime hora(String texto) {
        try {
            return LocalTime.parse(texto == null ? "" : texto.trim());
        } catch (DateTimeParseException error) {
            throw ErrorDeNegocio.solicitudInvalida("La hora debe tener el formato HH:mm.");
        }
    }
}
