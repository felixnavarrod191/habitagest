package com.habitagest.pqr;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

/** REQ-PH-031 y REQ-PH-032: publicar comunicados y mostrar solo los vigentes, del más reciente al más antiguo. */
@Service
public class ServicioComunicados {

    private final RepositorioComunicados comunicados;
    private final Clock reloj;

    public ServicioComunicados(RepositorioComunicados comunicados, Clock reloj) {
        this.comunicados = comunicados;
        this.reloj = reloj;
    }

    public Comunicado publicar(String titulo, String texto, LocalDate vigenteHasta) {
        if (titulo == null || titulo.isBlank() || texto == null || texto.isBlank() || vigenteHasta == null) {
            throw ErrorDeNegocio.solicitudInvalida("El título, el texto y la vigencia son obligatorios.");
        }
        if (vigenteHasta.isBefore(LocalDate.now(reloj))) {
            throw ErrorDeNegocio.solicitudInvalida("La vigencia no puede ser anterior a hoy.");
        }
        return comunicados.save(new Comunicado(titulo.trim(), texto.trim(), vigenteHasta, LocalDateTime.now(reloj)));
    }

    /** Un comunicado vigente se muestra hasta el final de su fecha de vigencia; después ya no aparece. */
    public List<Comunicado> vigentes() {
        LocalDate hoy = LocalDate.now(reloj);
        return comunicados.findAll().stream()
                .filter(comunicado -> !comunicado.getVigenteHasta().isBefore(hoy))
                .sorted(Comparator.comparing(Comunicado::getPublicado).reversed())
                .toList();
    }
}
