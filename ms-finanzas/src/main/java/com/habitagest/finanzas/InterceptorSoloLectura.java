package com.habitagest.finanzas;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * REQ-PH-017: el revisor fiscal solo puede consultar. El rol llega en el encabezado X-Rol (lo agrega la web);
 * cualquier operación que no sea de lectura con ese rol se rechaza con 403. La única excepción es dejar una
 * observación de auditoría (POST /observaciones, REQ-PH-044).
 */
@Component
public class InterceptorSoloLectura implements HandlerInterceptor, WebMvcConfigurer {

    static final String ROL_REVISOR_FISCAL = "REVISOR_FISCAL";

    @Override
    public boolean preHandle(HttpServletRequest peticion, HttpServletResponse respuesta, Object manejador) {
        String metodo = peticion.getMethod();
        boolean esLectura = "GET".equals(metodo) || "HEAD".equals(metodo) || "OPTIONS".equals(metodo);
        boolean dejaObservacion = "POST".equals(metodo) && "/observaciones".equals(peticion.getRequestURI());
        if (ROL_REVISOR_FISCAL.equalsIgnoreCase(peticion.getHeader("X-Rol")) && !esLectura && !dejaObservacion) {
            throw ErrorDeNegocio.sinPermiso("El revisor fiscal solo puede consultar.");
        }
        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registro.addInterceptor(this);
    }
}
