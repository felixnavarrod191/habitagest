package com.habitagest.copropiedad;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-004 a REQ-PH-007: torres, apartamentos con coeficiente, propietarios y residentes. */
@Service
public class ServicioCopropiedad {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final RepositorioTorres torres;
    private final RepositorioApartamentos apartamentos;
    private final RepositorioPropietarios propietarios;
    private final RepositorioResidentes residentes;

    public ServicioCopropiedad(RepositorioTorres torres, RepositorioApartamentos apartamentos,
            RepositorioPropietarios propietarios, RepositorioResidentes residentes) {
        this.torres = torres;
        this.apartamentos = apartamentos;
        this.propietarios = propietarios;
        this.residentes = residentes;
    }

    public Torre crearTorre(String nombre) {
        String limpio = nombre == null ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            throw ErrorDeNegocio.solicitudInvalida("El nombre de la torre es obligatorio.");
        }
        if (torres.existsByNombre(limpio)) {
            throw ErrorDeNegocio.reglaDeNegocio("Ya existe la torre " + limpio + ".");
        }
        return torres.save(new Torre(limpio));
    }

    public List<Torre> listarTorres() {
        return torres.findAll();
    }

    /** REQ-PH-004 y REQ-PH-005: guarda el apartamento y rechaza que los coeficientes sumen más de 100 %. */
    public Apartamento crearApartamento(String torre, String numero, double coeficiente) {
        String nombreTorre = torre == null ? "" : torre.trim();
        String nombreNumero = numero == null ? "" : numero.trim();
        if (nombreTorre.isEmpty() || nombreNumero.isEmpty()) {
            throw ErrorDeNegocio.solicitudInvalida("La torre y el número del apartamento son obligatorios.");
        }
        if (coeficiente <= 0 || coeficiente > 100) {
            throw ErrorDeNegocio.solicitudInvalida("El coeficiente debe ser mayor que 0 y no superar 100 %.");
        }
        if (!torres.existsByNombre(nombreTorre)) {
            throw ErrorDeNegocio.solicitudInvalida("La torre " + nombreTorre + " no existe.");
        }
        if (apartamentos.existsByTorreAndNumero(nombreTorre, nombreNumero)) {
            throw ErrorDeNegocio.reglaDeNegocio(
                    "Ya existe el apartamento " + nombreNumero + " en la torre " + nombreTorre + ".");
        }
        BigDecimal suma = BigDecimal.valueOf(coeficiente);
        for (Apartamento existente : apartamentos.findAll()) {
            suma = suma.add(BigDecimal.valueOf(existente.getCoeficiente()));
        }
        if (suma.compareTo(CIEN) > 0) {
            throw ErrorDeNegocio.reglaDeNegocio("La suma de los coeficientes sería "
                    + suma.stripTrailingZeros().toPlainString() + " %, que supera el 100 %.");
        }
        return apartamentos.save(new Apartamento(nombreTorre, nombreNumero, coeficiente));
    }

    public List<Apartamento> listarApartamentos(String torre) {
        if (torre == null || torre.isBlank()) {
            return apartamentos.findAll();
        }
        return apartamentos.findByTorre(torre.trim());
    }

    public Apartamento obtenerApartamento(String id) {
        return apartamentos.findById(id)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe el apartamento " + id + "."));
    }

    /** REQ-PH-006: el propietario queda asociado a su apartamento; cada apartamento tiene uno solo. */
    public Propietario crearPropietario(String nombre, String documento, String apartamentoId) {
        Apartamento apartamento = obtenerApartamento(apartamentoId);
        if (propietarios.existsByApartamentoId(apartamentoId)) {
            throw ErrorDeNegocio.reglaDeNegocio("El apartamento ya tiene un propietario responsable.");
        }
        Propietario propietario = propietarios.save(new Propietario(nombre.trim(), documento.trim(), apartamentoId));
        apartamento.setPropietarioId(propietario.getId());
        apartamentos.save(apartamento);
        return propietario;
    }

    public Propietario obtenerPropietario(String id) {
        return propietarios.findById(id)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No existe el propietario " + id + "."));
    }

    /** REQ-PH-007: el residente queda guardado en su apartamento. */
    public Residente crearResidente(String nombre, String documento, String apartamentoId) {
        obtenerApartamento(apartamentoId);
        return residentes.save(new Residente(nombre.trim(), documento.trim(), apartamentoId));
    }

    public List<Residente> listarResidentes(String apartamentoId) {
        obtenerApartamento(apartamentoId);
        return residentes.findByApartamentoId(apartamentoId);
    }
}
