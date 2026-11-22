# Bitácora de uso de IA del Sprint 3

Continúa la bitácora del Sprint 2 (`bitacora-sprint-2.md`). Registro de cada uso de la IA: qué se pidió, qué se aceptó, qué se descartó y cómo se verificó. La regla de oro: no se acepta código que nadie pueda explicar.

## 2026-11-17 · Planning S3 · Acta del Planning del Sprint 3 y bitácora

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** Planning S3 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el acta del Planning y la bitácora a partir del plan de proyecto y de las reglas del equipo.
- **Archivos generados:** docs/actas/sprint-3-planning.md, docs/ia/bitacora-sprint-3.md
- **Qué se aceptó:** el texto generado, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor compara responsables, fechas y puntos con el plan de proyecto del Excel.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-19 · T071 a T082 · PBI-08 PQR con flujo estricto de estados

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T071 a T082 · **Responsable de la tarjeta:** Felix José Navarro Ortiz · **Revisa:** Angel Daniel Moreno Lazcano
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-pqr/ (Pqr, Cambio, PqrNueva, CambioEstado, CierrePqr, RepositorioPqr, ServicioPqr, ControladorPqr y sus pruebas)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-20 · T083 a T086 · PBI-09 Comunicados

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T083 a T086 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-pqr/ (Comunicado, ComunicadoNuevo, RepositorioComunicados, ServicioComunicados, ControladorComunicados y su prueba)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-20 · T087 a T096 · PBI-10 Portería y seguridad

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T087 a T096 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Estiven Alexander Rodríguez Pérez
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-porteria/ (Visita, Ingreso, Paquete, Novedad, repositorios, ClienteCopropiedad, ServicioPorteria, ControladorPorteria y sus pruebas)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-21 · T097 a T106 · PBI-11 Mantenimiento con aprobación del Consejo

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T097 a T106 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Angel Daniel Moreno Lazcano
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-mantenimiento/ (Orden, Material, Cambio, OrdenNueva, Aprobacion, Ejecucion, GastosMes, RepositorioOrdenes, ServicioOrdenes, ControladorOrdenes y sus pruebas)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.
