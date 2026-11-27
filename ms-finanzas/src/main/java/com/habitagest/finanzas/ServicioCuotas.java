package com.habitagest.finanzas;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-008 a REQ-PH-010: presupuesto mensual y generación de las cuotas por coeficiente. */
@Service
public class ServicioCuotas {

    /** Parámetro del negocio: las cuotas vencen el día 10 de cada mes. */
    static final int DIA_DE_VENCIMIENTO = 10;

    private final RepositorioPresupuestos presupuestos;
    private final RepositorioCuotas cuotas;
    private final ClienteCopropiedad copropiedad;
    private final Clock reloj;

    public ServicioCuotas(RepositorioPresupuestos presupuestos, RepositorioCuotas cuotas, ClienteCopropiedad copropiedad,
            Clock reloj) {
        this.presupuestos = presupuestos;
        this.cuotas = cuotas;
        this.copropiedad = copropiedad;
        this.reloj = reloj;
    }

    /** REQ-PH-008: guarda el presupuesto del mes. */
    public Presupuesto guardarPresupuesto(String mes, double valor) {
        YearMonth periodo = periodo(mes);
        if (valor <= 0) {
            throw ErrorDeNegocio.solicitudInvalida("El valor del presupuesto debe ser mayor que 0.");
        }
        if (presupuestos.existsByMes(periodo.toString())) {
            throw ErrorDeNegocio.reglaDeNegocio("Ya existe un presupuesto para " + periodo + ".");
        }
        return presupuestos.save(new Presupuesto(periodo.toString(), valor));
    }

    public Presupuesto consultarPresupuesto(String mes) {
        String clave = periodo(mes).toString();
        return presupuestos.findByMes(clave)
                .orElseThrow(() -> ErrorDeNegocio.noEncontrado("No hay presupuesto para " + clave + "."));
    }

    /** REQ-PH-008: todos los presupuestos guardados, del mes más reciente al más antiguo. */
    public List<Presupuesto> listarPresupuestos() {
        List<Presupuesto> lista = new ArrayList<>(presupuestos.findAll());
        lista.sort(Comparator.comparing(Presupuesto::getMes).reversed());
        return lista;
    }

    /** REQ-PH-009 y REQ-PH-010: genera las cuotas del mes, o informa por qué no las genera. */
    public List<CuotaVista> generarCuotas(String mes) {
        YearMonth periodo = periodo(mes);
        String clave = periodo.toString();
        Presupuesto presupuesto = presupuestos.findByMes(clave).orElseThrow(() -> ErrorDeNegocio
                .reglaDeNegocio("No hay presupuesto para " + clave + ": no se generaron cuotas."));
        if (cuotas.existsByMes(clave)) {
            throw ErrorDeNegocio.reglaDeNegocio("Las cuotas de " + clave + " ya fueron generadas.");
        }
        List<ApartamentoRemoto> apartamentos = copropiedad.apartamentos(null);
        if (apartamentos.isEmpty()) {
            throw ErrorDeNegocio.reglaDeNegocio("No hay apartamentos registrados para generar cuotas.");
        }
        List<BigDecimal> coeficientes = new ArrayList<>();
        for (ApartamentoRemoto apartamento : apartamentos) {
            coeficientes.add(BigDecimal.valueOf(apartamento.coeficiente()));
        }
        List<BigDecimal> valores = CalculoCuotas.repartir(BigDecimal.valueOf(presupuesto.getValor()), coeficientes);
        LocalDate vencimiento = periodo.atDay(DIA_DE_VENCIMIENTO);
        LocalDate hoy = LocalDate.now(reloj);
        List<CuotaVista> generadas = new ArrayList<>();
        for (int i = 0; i < apartamentos.size(); i++) {
            Cuota cuota = cuotas.save(
                    new Cuota(apartamentos.get(i).id(), clave, valores.get(i).doubleValue(), vencimiento));
            generadas.add(CuotaVista.de(cuota, hoy));
        }
        return generadas;
    }

    public List<CuotaVista> consultarCuotas(String apartamentoId, String mes) {
        boolean conApartamento = apartamentoId != null && !apartamentoId.isBlank();
        boolean conMes = mes != null && !mes.isBlank();
        List<Cuota> encontradas;
        if (conApartamento && conMes) {
            encontradas = cuotas.findByApartamentoIdAndMes(apartamentoId, periodo(mes).toString());
        } else if (conApartamento) {
            encontradas = cuotas.findByApartamentoId(apartamentoId);
        } else if (conMes) {
            encontradas = cuotas.findByMes(periodo(mes).toString());
        } else {
            encontradas = cuotas.findAll();
        }
        LocalDate hoy = LocalDate.now(reloj);
        List<CuotaVista> vistas = new ArrayList<>();
        for (Cuota cuota : encontradas) {
            vistas.add(CuotaVista.de(cuota, hoy));
        }
        return vistas;
    }

    static YearMonth periodo(String mes) {
        try {
            return YearMonth.parse(mes == null ? "" : mes.trim());
        } catch (DateTimeParseException error) {
            throw ErrorDeNegocio.solicitudInvalida("El mes debe tener el formato AAAA-MM.");
        }
    }
}
