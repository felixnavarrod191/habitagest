package com.habitagest.reservas;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

/** NFR-PH-005: sin MONGODB_URI el servicio no arranca y lo informa con un mensaje claro. */
class ConfiguracionObligatoriaTest {

    @Test
    void sinLaVariableFallaConUnMensajeClaro() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> ConfiguracionObligatoria.exigir(Map.of(), "MONGODB_URI"));
        assertTrue(error.getMessage().contains("MONGODB_URI"));
    }

    @Test
    void conLaVariableVaciaFalla() {
        assertThrows(IllegalStateException.class,
                () -> ConfiguracionObligatoria.exigir(Map.of("MONGODB_URI", " "), "MONGODB_URI"));
    }

    @Test
    void conLaVariableDefinidaNoFalla() {
        assertDoesNotThrow(() -> ConfiguracionObligatoria.exigir(Map.of("MONGODB_URI", "mongodb://localhost"), "MONGODB_URI"));
    }
}
