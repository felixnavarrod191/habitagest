# ADR-003 · Autenticación y autorización con Spring Security y sesión en la web

- **Estado:** aceptado (Puerta 2)
- **Fecha:** 2026-11-08
- **Requisitos relacionados:** REQ-PH-001, REQ-PH-002, REQ-PH-003, NFR-PH-002

## Contexto

Se necesitan 11 roles con acceso a sus propias pantallas y contraseñas protegidas, con el menor esfuerzo posible.

## Decisión

La autenticación y la autorización por rol viven en el módulo web con Spring Security, sesión de servidor y contraseñas con BCrypt. Los roles son un enum y las rutas se protegen por prefijo.

## Alternativas consideradas

- Un servicio de identidad aparte con tokens: es más flexible pero excede el alcance del curso.
- Autenticación en cada microservicio: duplica la lógica en los 6 servicios.

## Consecuencias

Los servicios de negocio confían en el gateway y la web; no exponen una autenticación propia. Si se abrieran a otros clientes habría que añadirla.
