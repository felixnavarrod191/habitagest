# Bitácora de uso de IA del Sprint 2

Continúa la bitácora del Sprint 1 (`bitacora.md`). Registro de cada uso de la IA: qué se pidió, qué se aceptó, qué se descartó y cómo se verificó. La regla de oro: no se acepta código que nadie pueda explicar.

## 2026-11-12 · Planning S2 · Acta del Planning del Sprint 2 y bitácora

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** Planning S2 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Estiven Alexander Rodríguez Pérez
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** docs/actas/sprint-2-planning.md, docs/ia/bitacora-sprint-2.md
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · T026 a T028 · Fundamentos: errores comunes, auditoría y rutas del gateway

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T026 a T028 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-*/ (ErrorApi, ErrorDeNegocio, ManejadorErrores, ConfiguracionComun, RegistroAuditoria, ServicioAuditoria) y api-gateway/ (RutasGatewayTest)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · PBI-04 · Copropiedad: torres, apartamentos, propietarios y residentes

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** PBI-04 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-copropiedad/
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · PBI-05 · Presupuesto y cuotas por coeficiente

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** PBI-05 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-finanzas/ (presupuestos y cuotas)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · PBI-06 · Pagos, mora y estado de cuenta

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** PBI-06 · **Responsable de la tarjeta:** Felix José Navarro Ortiz · **Revisa:** Angel Daniel Moreno Lazcano
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-finanzas/ (pagos, mora, cartera y estado de cuenta)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · PBI-07 · Reservas de zonas comunes sin cruces

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** PBI-07 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-reservas/
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-12 · Cierre S2 · tasks.md, trazabilidad y contratos del Sprint 2

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** Cierre S2 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Angel Daniel Moreno Lazcano
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** specs/001-habitagest/tasks.md, docs/trazabilidad.md, specs/001-habitagest/contracts/
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.
