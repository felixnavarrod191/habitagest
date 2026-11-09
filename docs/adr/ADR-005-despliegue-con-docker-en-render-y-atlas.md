# ADR-005 · Despliegue con Docker en Render y MongoDB Atlas

- **Estado:** aceptado (Puerta 2)
- **Fecha:** 2026-11-08
- **Requisitos relacionados:** NFR-PH-005, HU-33

## Contexto

El curso exige servicios en contenedores desplegados en la nube con un costo cero.

## Decisión

Cada módulo tiene un Dockerfile de dos etapas y un servicio de Render. Las bases viven en un clúster de MongoDB Atlas. Toda la configuración va en variables de entorno.

## Alternativas consideradas

- Otro proveedor de nube o instalación propia: no está dentro de las restricciones del curso.
- Desplegar sin contenedores: pierde la reproducibilidad entre el entorno local y Render.

## Consecuencias

El plan gratuito tiene límites de memoria y los servicios se duermen: las demostraciones requieren despertarlos antes.
