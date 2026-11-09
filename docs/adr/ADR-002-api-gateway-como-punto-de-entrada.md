# ADR-002 · API Gateway como punto de entrada

- **Estado:** aceptado (Puerta 2)
- **Fecha:** 2026-11-08
- **Requisitos relacionados:** Todos los requisitos funcionales

## Contexto

La web necesita conocer una sola dirección y se quiere un lugar único para enrutar y, más adelante, aplicar reglas comunes.

## Decisión

La aplicación web llama al API Gateway (Spring Cloud Gateway), que enruta cada prefijo (/api/copropiedad, /api/finanzas…) al servicio dueño. Las direcciones de los servicios llegan por variables de entorno.

## Alternativas consideradas

- Que la web llame a cada servicio directamente: expone todas las direcciones y duplica la configuración.
- Un balanceador externo: añade infraestructura que el curso no pide.

## Consecuencias

Hay un módulo más que desplegar y mantener, y un punto único por el que pasa todo el tráfico.
