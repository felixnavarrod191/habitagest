package com.habitagest.reservas;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** T027: todos los errores salen con el cuerpo {codigo, mensaje} del contrato. */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ErrorDeNegocio.class)
    public ResponseEntity<ErrorApi> negocio(ErrorDeNegocio error) {
        return respuesta(error.estado(), error.codigo(), error.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorApi> validacion(MethodArgumentNotValidException error) {
        String mensaje = error.getBindingResult().getFieldErrors().stream()
                .map(campo -> campo.getField() + ": " + campo.getDefaultMessage())
                .findFirst()
                .orElse("La solicitud no es válida.");
        return respuesta(400, "E400", mensaje);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorApi> solicitudInvalida(Exception error) {
        return respuesta(400, "E400", "La solicitud no es válida: revisa los datos enviados.");
    }

    private static ResponseEntity<ErrorApi> respuesta(int estado, String codigo, String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorApi(codigo, mensaje));
    }
}
