package com.habitagest.porteria;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-033 a REQ-PH-037: visitas autorizadas, ingresos y salidas, paquetes y novedades de seguridad. */
@Service
public class ServicioPorteria {

    private final RepositorioVisitas visitas;
    private final RepositorioIngresos ingresos;
    private final RepositorioPaquetes paquetes;
    private final RepositorioNovedades novedades;
    private final ClienteCopropiedad copropiedad;
    private final ServicioAuditoria auditoria;
    private final Clock reloj;

    public ServicioPorteria(RepositorioVisitas visitas, RepositorioIngresos ingresos, RepositorioPaquetes paquetes,
            RepositorioNovedades novedades, ClienteCopropiedad copropiedad, ServicioAuditoria auditoria, Clock reloj) {
        this.visitas = visitas;
        this.ingresos = ingresos;
        this.paquetes = paquetes;
        this.novedades = novedades;
        this.copropiedad = copropiedad;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    /** REQ-PH-033: el residente autoriza a una persona para una fecha y el portero la ve como autorizada. */
    public Visita autorizar(String apartamentoId, String nombre, String documento, LocalDate fecha) {
        if (vacio(apartamentoId) || vacio(nombre) || vacio(documento) || fecha == null) {
            throw ErrorDeNegocio.solicitudInvalida("El apartamento, el nombre, el documento y la fecha son obligatorios.");
        }
        if (fecha.isBefore(LocalDate.now(reloj))) {
            throw ErrorDeNegocio.solicitudInvalida("La fecha de la visita no puede ser anterior a hoy.");
        }
        if (!copropiedad.existeApartamento(apartamentoId.trim())) {
            throw ErrorDeNegocio.noEncontrado("No existe el apartamento " + apartamentoId + ".");
        }
        return visitas.save(new Visita(apartamentoId.trim(), nombre.trim(), documento.trim(), fecha));
    }

    /** Las visitas autorizadas de una fecha (formato AAAA-MM-DD) con su estado. */
    public List<Visita> visitasDeLaFecha(String fecha) {
        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha == null ? "" : fecha.trim());
        } catch (DateTimeParseException error) {
            throw ErrorDeNegocio.solicitudInvalida("La fecha debe tener el formato AAAA-MM-DD.");
        }
        return visitas.findByFecha(dia).stream().sorted(Comparator.comparing(Visita::getNombre)).toList();
    }

    /** REQ-PH-034: los ingresos y salidas, con su hora, de las visitas autorizadas para una fecha (AAAA-MM-DD). */
    public List<Ingreso> ingresosDeLaFecha(String fecha) {
        List<Ingreso> registros = new ArrayList<>();
        for (Visita visita : visitasDeLaFecha(fecha)) {
            registros.addAll(ingresos.findByVisitaId(visita.getId()));
        }
        registros.sort(Comparator.comparing(Ingreso::getHora));
        return registros;
    }

    /**
     * REQ-PH-034 y REQ-PH-035: solo se registra el ingreso de una visita autorizada, el día de la visita;
     * la salida exige un ingreso previo. Cada registro guarda la hora y quién lo hizo.
     */
    public Ingreso registrar(String visitaId, TipoIngreso tipo, String usuario) {
        Visita visita = visitas.findById(visitaId == null ? "" : visitaId)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado(
                        "El visitante no fue autorizado por ningún residente (RN-10)."));
        LocalDateTime ahora = LocalDateTime.now(reloj);
        if (!visita.getFecha().equals(ahora.toLocalDate())) {
            throw ErrorDeNegocio.reglaDeNegocio("La visita está autorizada para el " + visita.getFecha() + ", no para hoy.");
        }
        String anterior = visita.getEstado();
        if (tipo == TipoIngreso.ENTRADA) {
            if (!Visita.AUTORIZADA.equals(anterior)) {
                throw ErrorDeNegocio.reglaDeNegocio("La visita ya registró su ingreso.");
            }
            visita.setEstado(Visita.DENTRO);
        } else {
            if (!Visita.DENTRO.equals(anterior)) {
                throw ErrorDeNegocio.reglaDeNegocio("La salida solo se registra después del ingreso.");
            }
            visita.setEstado(Visita.SALIO);
        }
        visitas.save(visita);
        String quien = vacio(usuario) ? "sistema" : usuario.trim();
        auditoria.registrar("Visita", visita.getId(), anterior, visita.getEstado(), quien);
        return ingresos.save(new Ingreso(visita.getId(), tipo.name(), ahora, quien));
    }

    /** REQ-PH-036: el paquete queda pendiente de recoger y el residente lo ve en su apartamento. */
    public Paquete registrarPaquete(String apartamentoId, String descripcion) {
        if (vacio(apartamentoId) || vacio(descripcion)) {
            throw ErrorDeNegocio.solicitudInvalida("El apartamento y la descripción son obligatorios.");
        }
        return paquetes.save(new Paquete(apartamentoId.trim(), descripcion.trim(), LocalDateTime.now(reloj)));
    }

    public List<Paquete> paquetesDelApartamento(String apartamentoId) {
        if (vacio(apartamentoId)) {
            throw ErrorDeNegocio.solicitudInvalida("El apartamento es obligatorio.");
        }
        return paquetes.findByApartamentoId(apartamentoId.trim()).stream()
                .sorted(Comparator.comparing(Paquete::getRecibido).reversed()).toList();
    }

    public Paquete entregarPaquete(String id, String usuario) {
        Paquete paquete = paquetes.findById(id)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe el paquete " + id + "."));
        if (Paquete.ENTREGADO.equals(paquete.getEstado())) {
            throw ErrorDeNegocio.reglaDeNegocio("El paquete ya fue entregado.");
        }
        paquete.setEstado(Paquete.ENTREGADO);
        paquetes.save(paquete);
        auditoria.registrar("Paquete", paquete.getId(), Paquete.PENDIENTE, Paquete.ENTREGADO, usuario);
        return paquete;
    }

    /** REQ-PH-037: la novedad se guarda con fecha, hora y autor. */
    public Novedad registrarNovedad(String texto, String autor) {
        if (vacio(texto)) {
            throw ErrorDeNegocio.solicitudInvalida("El texto de la novedad es obligatorio.");
        }
        return novedades.save(new Novedad(texto.trim(), vacio(autor) ? "sistema" : autor.trim(), LocalDateTime.now(reloj)));
    }

    public List<Novedad> novedades() {
        return novedades.findAll().stream().sorted(Comparator.comparing(Novedad::getFecha).reversed()).toList();
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
