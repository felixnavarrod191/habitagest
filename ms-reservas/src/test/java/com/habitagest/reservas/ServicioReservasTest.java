package com.habitagest.reservas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Pruebas de REQ-PH-018 a REQ-PH-024 (AC-018.1 a AC-024.1). */
class ServicioReservasTest {

    private static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final LocalDate SABADO = LocalDate.of(2026, 11, 21);

    private final RepositorioReservas reservas = RepositorioEnMemoria.crear(RepositorioReservas.class);
    private final RepositorioAuditoria registros = RepositorioEnMemoria.crear(RepositorioAuditoria.class);
    private final Set<String> enMora = new HashSet<>();
    private final Set<String> existentes = new HashSet<>(Set.of("a1", "a2", "a3"));

    private ServicioReservas servicioEn(String instanteUtc) {
        Clock reloj = Clock.fixed(Instant.parse(instanteUtc), ZONA);
        return new ServicioReservas(RepositorioEnMemoria.crear(RepositorioZonas.class), reservas, enMora::contains,
                existentes::contains, new ServicioAuditoria(registros, reloj), reloj);
    }

    /** Jueves 19 de noviembre de 2026, 10:00 hora de Colombia. */
    private ServicioReservas servicioElJueves() {
        return servicioEn("2026-11-19T15:00:00Z");
    }

    private static LocalDateTime sabado(int hora) {
        return SABADO.atTime(hora, 0);
    }

    /** AC-018.1: la zona salón social con horario de 8:00 a 22:00 admite reservas solo dentro de ese horario. */
    @Test
    void laZonaAdmiteReservasSoloDentroDeSuHorario() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");

        assertEquals("08:00", servicio.listarZonas().get(0).getHoraInicio());
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a2", sabado(6), sabado(8), "r")).estado());
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a2", sabado(20), sabado(23).plusDays(0), "r")).estado());
    }

    @Test
    void rechazaZonasConHorarioSinSentidoORepetidas() {
        ServicioReservas servicio = servicioElJueves();
        servicio.crearZona("Salón social", "08:00", "22:00");

        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crearZona("BBQ", "22:00", "08:00")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.crearZona("BBQ", "ocho", "22:00")).estado());
        assertEquals(409, assertThrows(ErrorDeNegocio.class, () -> servicio.crearZona("Salón social", "08:00", "22:00")).estado());
    }

    /** AC-019.1: con una reserva de 14:00 a 16:00, otro residente ve ese horario ocupado. */
    @Test
    void elHorarioReservadoApareceOcupado() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        List<Horario> horarios = servicio.disponibilidad(salon.getId(), "2026-11-21");

        assertEquals(3, horarios.size());
        assertEquals(Horario.LIBRE, horarios.get(0).estado());
        assertEquals(sabado(8), horarios.get(0).inicio());
        assertEquals(sabado(14), horarios.get(0).fin());
        assertEquals(Horario.OCUPADO, horarios.get(1).estado());
        assertEquals(sabado(14), horarios.get(1).inicio());
        assertEquals(sabado(16), horarios.get(1).fin());
        assertEquals(Horario.LIBRE, horarios.get(2).estado());
        assertEquals(sabado(22), horarios.get(2).fin());
    }

    @Test
    void sinReservasTodoElHorarioDeLaZonaEstaLibre() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");

        List<Horario> horarios = servicio.disponibilidad(salon.getId(), "2026-11-21");

        assertEquals(1, horarios.size());
        assertEquals(Horario.LIBRE, horarios.get(0).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class, () -> servicio.disponibilidad(salon.getId(), "21/11")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class, () -> servicio.disponibilidad("x", "2026-11-21")).estado());
    }

    /** AC-020.1: un horario libre y un apartamento al día: la reserva de 14:00 a 16:00 queda confirmada. */
    @Test
    void confirmaLaReservaEnUnHorarioLibre() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");

        Reserva reserva = servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        assertEquals(Reserva.CONFIRMADA, reserva.getEstado());
        assertEquals(1, registros.findAll().size());
        assertEquals("residente1", registros.findAll().get(0).getUsuario());
    }

    /** AC-021.1: con una reserva de 14:00 a 16:00, otra de 15:00 a 17:00 se rechaza por cruce de horario. */
    @Test
    void rechazaLaReservaConCruceDeHorario() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a2", sabado(15), sabado(17), "residente2"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("cruce"));
    }

    @Test
    void dosReservasSeguidasSinTraslapeSeAceptan() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        Reserva siguiente = servicio.reservar(salon.getId(), "a2", sabado(16), sabado(18), "residente2");

        assertEquals(Reserva.CONFIRMADA, siguiente.getEstado());
    }

    /** AC-022.1: un apartamento en mora no puede reservar y el sistema indica que hay mora. */
    @Test
    void rechazaLaReservaDeUnApartamentoEnMora() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        enMora.add("a1");

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("mora"));
        assertEquals(0, reservas.findAll().size());
    }

    /** AC-022.2: un apartamento con 2 reservas en el mes no puede hacer una tercera e indica el máximo mensual. */
    @Test
    void rechazaLaTerceraReservaDelMes() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        servicio.reservar(salon.getId(), "a1", sabado(9), sabado(11), "residente1");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a1", sabado(18), sabado(20), "residente1"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("máximo de 2"));
    }

    @Test
    void lasReservasCanceladasNoCuentanParaElMaximoDelMes() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        Reserva primera = servicio.reservar(salon.getId(), "a1", sabado(9), sabado(11), "residente1");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");
        servicio.cancelar(primera.getId(), Solicitante.RESIDENTE, "residente1");

        Reserva nueva = servicio.reservar(salon.getId(), "a1", sabado(18), sabado(20), "residente1");

        assertEquals(Reserva.CONFIRMADA, nueva.getEstado());
    }

    @Test
    void rechazaReservasDeMasDeCuatroHorasYApartamentosQueNoExisten() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");

        ErrorDeNegocio largo = assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a1", sabado(10), sabado(15), "r"));
        assertEquals(409, largo.estado());
        assertTrue(largo.getMessage().contains("4 horas"));
        assertEquals(404, assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "zzz", sabado(10), sabado(12), "r")).estado());
        assertEquals(400, assertThrows(ErrorDeNegocio.class,
                () -> servicio.reservar(salon.getId(), "a1", sabado(12), sabado(10), "r")).estado());
        assertEquals(4, java.time.Duration.between(sabado(10), sabado(14)).toHours());
        assertEquals(Reserva.CONFIRMADA, servicio.reservar(salon.getId(), "a1", sabado(10), sabado(14), "r").getEstado());
    }

    /** AC-023.1: una reserva para el sábado a las 14:00 cancelada el jueves libera el horario. */
    @Test
    void elResidenteCancelaConMasDe24HorasYElHorarioQuedaLibre() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        Reserva reserva = servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");

        Reserva cancelada = servicio.cancelar(reserva.getId(), Solicitante.RESIDENTE, "residente1");

        assertEquals(Reserva.CANCELADA, cancelada.getEstado());
        assertEquals(1, servicio.disponibilidad(salon.getId(), "2026-11-21").size());
        assertEquals(Reserva.CANCELADA, registros.findAll().get(1).getEstadoNuevo());
    }

    /** AC-024.1: el residente que intenta cancelar el sábado a las 10:00 una reserva de las 14:00 es rechazado. */
    @Test
    void elResidenteNoCancelaConMenosDe24Horas() {
        ServicioReservas reservador = servicioElJueves();
        Zona salon = reservador.crearZona("Salón social", "08:00", "22:00");
        Reserva reserva = reservador.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");
        ServicioReservas elSabado = servicioEn("2026-11-21T15:00:00Z");

        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class,
                () -> elSabado.cancelar(reserva.getId(), Solicitante.RESIDENTE, "residente1"));

        assertEquals(409, error.estado());
        assertTrue(error.getMessage().contains("24 horas"));
        assertEquals(Reserva.CONFIRMADA, reservas.findById(reserva.getId()).get().getEstado());
    }

    @Test
    void elAdministradorCancelaEnCualquierMomento() {
        ServicioReservas reservador = servicioElJueves();
        Zona salon = reservador.crearZona("Salón social", "08:00", "22:00");
        Reserva reserva = reservador.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");
        ServicioReservas elSabado = servicioEn("2026-11-21T15:00:00Z");

        Reserva cancelada = elSabado.cancelar(reserva.getId(), Solicitante.ADMINISTRADOR, "admin");

        assertEquals(Reserva.CANCELADA, cancelada.getEstado());
        assertEquals(409, assertThrows(ErrorDeNegocio.class,
                () -> elSabado.cancelar(reserva.getId(), Solicitante.ADMINISTRADOR, "admin")).estado());
        assertEquals(404, assertThrows(ErrorDeNegocio.class,
                () -> elSabado.cancelar("no-existe", Solicitante.ADMINISTRADOR, "admin")).estado());
    }

    /** El residente consulta sus reservas (la web las muestra por apartamento). */
    @Test
    void laListaDeReservasSePuedeFiltrarPorApartamento() {
        ServicioReservas servicio = servicioElJueves();
        Zona salon = servicio.crearZona("Salón social", "08:00", "22:00");
        servicio.reservar(salon.getId(), "a1", sabado(14), sabado(16), "residente1");
        servicio.reservar(salon.getId(), "a2", sabado(17), sabado(19), "residente2");

        assertEquals(2, servicio.listarReservas(null).size());
        assertEquals(1, servicio.listarReservas("a2").size());
        assertEquals(sabado(17), servicio.listarReservas("a2").get(0).getInicio());
    }
}
