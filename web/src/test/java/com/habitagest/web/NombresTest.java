package com.habitagest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.habitagest.web.pantallas.Campo;
import com.habitagest.web.pantallas.Pantallas;
import java.time.YearMonth;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/** Nombres de torre sin repetir la palabra "Torre" y selector de mes con calendario. */
class NombresTest {

    @Test
    void elNombreDeLaTorreNoRepiteLaPalabraTorre() {
        assertEquals("Torre 1", Pantallas.nombreTorre("1"));
        assertEquals("Torre 2", Pantallas.nombreTorre("Torre 2"));
        assertEquals("Torre 3", Pantallas.nombreTorre(" torre 3 "));
    }

    @Test
    void elCampoDeMesUsaCalendarioYProponeElMesActual() {
        Campo campo = Pantallas.mes("mes", "Mes");
        assertEquals("month", campo.tipo());
        assertEquals(YearMonth.now(ZoneId.of("America/Bogota")).toString(), campo.valor());
    }
}
