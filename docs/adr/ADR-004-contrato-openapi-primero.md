# ADR-004 · Contrato OpenAPI primero

- **Estado:** aceptado (Puerta 2)
- **Fecha:** 2026-11-08
- **Requisitos relacionados:** NFR-PH-006

## Contexto

Si la API se documenta después del código, el contrato deja de coincidir con el comportamiento.

## Decisión

Cada servicio tiene su contrato OpenAPI en `specs/001-habitagest/contracts/` antes de implementarse, y las pruebas de contrato verifican que el servicio lo cumple. Swagger publica el contrato en cada servicio.

## Alternativas consideradas

- Documentar la API generándola desde el código: es más rápido pero el contrato describe lo que existe, no lo que se acordó.
- No documentar: contradice la regla de trazabilidad.

## Consecuencias

Escribir el contrato antes tiene un costo inicial, a cambio de menos discrepancias entre equipos y pruebas.
