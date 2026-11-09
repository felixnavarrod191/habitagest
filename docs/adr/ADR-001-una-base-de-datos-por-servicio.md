# ADR-001 · Una base de datos por servicio

- **Estado:** aceptado (Puerta 2)
- **Fecha:** 2026-11-08
- **Requisitos relacionados:** NFR-PH-005, constitución (principio 3)

## Contexto

Cada microservicio es dueño de sus datos. Compartir una base crea acoplamiento oculto entre servicios y obliga a desplegarlos y cambiarlos juntos.

## Decisión

Cada servicio usa su propia base de datos en MongoDB Atlas (copropiedad_db, finanzas_db, reservas_db, pqr_db, porteria_db, mantenimiento_db y web_db). Los servicios se piden los datos por REST.

## Alternativas consideradas

- Una sola base compartida: más simple de arrancar, pero acopla los servicios y rompe el principio 3 de la constitución.
- Un esquema por servicio dentro de una misma base: mantiene el acoplamiento operativo y de acceso.

## Consecuencias

Hay más llamadas REST entre servicios y no hay uniones entre colecciones de distintos servicios. A cambio, cada servicio evoluciona y se despliega solo.
