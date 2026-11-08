# AGENTS.md — HabitaGest

Instrucciones permanentes para el agente de IA. Léelas antes de cada tarea. No copian la especificación: la enlazan.

## Qué es el proyecto

Sistema de gestión de un conjunto residencial (cuotas y mora, reservas, PQR, portería y mantenimiento) con 11 roles. Microservicios con una base de datos MongoDB por servicio. Reglas del proyecto: `constitution.md`. Qué se construye: `specs/001-habitagest/spec.md`. Cómo: `specs/001-habitagest/plan.md`. Qué sigue: `specs/001-habitagest/tasks.md`.

## Comandos

- Compilar y probar un módulo: `mvn verify` (desde la carpeta del módulo).
- Arrancar un servicio: `mvn spring-boot:run` con la variable `MONGODB_URI` definida en el entorno.
- Los puertos: ms-copropiedad 8081, ms-finanzas 8082, ms-reservas 8083, ms-pqr 8084, ms-porteria 8085, ms-mantenimiento 8086, web 8080, api-gateway 8090.

## Estructura

- `ms-copropiedad`, `ms-finanzas`, `ms-reservas`, `ms-pqr`, `ms-porteria`, `ms-mantenimiento`: un servicio por área, cada uno con su base de datos.
- `api-gateway`: punto único de entrada. `web`: interfaz por rol, usuarios y seguridad.
- `specs/001-habitagest/`: spec, plan, tasks y contratos OpenAPI. `docs/`: acuerdos, trazabilidad, ADR, bitácora de IA y actas.

## Reglas

- Antes de codificar, lee `constitution.md` y los documentos de `specs/001-habitagest/`.
- Trabaja una sola tarea (T###) por vez. Cita el requisito (REQ) y el criterio (AC) en el commit y en el pull request.
- Escribe primero las pruebas derivadas de los criterios de aceptación; después, la implementación.
- No agregues funcionalidad ni dependencias que ningún requisito pidió. Si crees que hace falta una, propón un ADR.
- No cambies la especificación. Si ves un error o una mejora, anótala como sugerencia.
- Un servicio nunca lee la base de datos de otro: pide el dato por REST al servicio dueño.
- Sin secretos ni datos personales reales en el código, en los prompts ni en las pruebas. La conexión a la base de datos va siempre en `MONGODB_URI`.
- Todo cambio de estado de PQR y de órdenes de mantenimiento se audita (usuario y fecha-hora).

## Prompt de implementación

Cada tarea se pide con seis partes: contexto, tarea, referencias, restricciones, criterio de salida y límites. Ejemplo en `docs/ia/bitacora.md`.
