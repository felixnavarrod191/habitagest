# Bitácora de uso de IA del Sprint 4

Continúa la bitácora del Sprint 3 (`bitacora-sprint-3.md`). Registro de cada uso de la IA: qué se pidió, qué se aceptó, qué se descartó y cómo se verificó. La regla de oro: no se acepta código que nadie pueda explicar.

## 2026-11-24 · Planning S4 · Acta del Planning del Sprint 4 y bitácora

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** Planning S4 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** el acta del Planning y la bitácora a partir del plan de proyecto y de las reglas del equipo.
- **Archivos generados:** docs/actas/sprint-4-planning.md, docs/ia/bitacora-sprint-4.md
- **Qué se aceptó:** el texto generado, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** el revisor compara responsables, fechas y puntos con el plan de proyecto del Excel.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-26 · T107 a T110 · PBI-12 Resumen de cartera y observaciones de auditoría

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T107 a T110 · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Estiven Alexander Rodríguez Pérez
- **Qué se pidió:** el código y las pruebas de esta tarjeta a partir de la spec v1.0, del plan, de los contratos y de tasks.md, con las seis partes del prompt: contexto, tarea, referencias, restricciones, criterio de salida y límites.
- **Archivos generados:** ms-finanzas/ (Observacion, ObservacionNueva, RepositorioObservaciones, ServicioObservaciones, ControladorObservaciones, ResumenFinanciero, ClienteMantenimiento, ClienteMantenimientoHttp, ServicioResumen, ControladorResumen y sus pruebas), docs/adr/ADR-006-resumen-en-finanzas-consulta-a-mantenimiento.md
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** que el Consejo consultara las observaciones, porque ninguna historia ni pantalla lo pide; se limitó a revisor fiscal y administrador del conjunto.
- **Cómo se verificó:** el revisor lee el diff completo y completa la lista de comprobación del pull request; se ejecuta `mvn verify` del módulo y se prueba el servicio arrancado.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-26 · Pantallas del resumen y de las observaciones

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** modificación aparte de pantallas por rol (sin T###) · **Responsable de la tarjeta:** Angel Daniel Moreno Lazcano · **Revisa:** Estiven Alexander Rodríguez Pérez
- **Qué se pidió:** las pantallas de resumen del Consejo y de observaciones del revisor fiscal y del administrador del conjunto.
- **Archivos generados:** web/ (ControladorRevision, ControladorConsejo, ControladorAdministracion, Areas y ResumenYObservacionesTest)
- **Qué se aceptó:** el código y las pruebas generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** `mvn verify` del módulo web (incluye `PantallasPorRolTest`) y recorrido de las pantallas con cada rol.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-26 · T113 a T120 y T122 · PBI-13 Despliegue de los servicios en Render

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T113 a T120 y T122 · **Responsable de la tarjeta:** Felix José Navarro Ortiz · **Revisa:** Angel Daniel Moreno Lazcano
- **Qué se pidió:** el Blueprint de Render, la guía de despliegue, el script de verificación y las pruebas de contrato de cada servicio contra su OpenAPI.
- **Archivos generados:** render.yaml, docs/despliegue-render.md, verificar-despliegue.ps1, ContratoOpenApiTest en los 6 servicios de negocio
- **Qué se aceptó:** los archivos generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** usar `fromService` con `hostport` para las direcciones entre servicios, porque las aplicaciones necesitan la dirección con `https://`; se usaron las direcciones públicas con el nombre de cada servicio.
- **Cómo se verificó:** `mvn verify` de cada módulo; el despliegue real se comprueba con `verificar-despliegue.ps1` una vez creado el Blueprint en Render.
- **Ajustes tras la verificación:** ninguno registrado.

## 2026-11-26 · T111, T112, T121, T123 y T124 · PBI-14 Calidad, trazabilidad y release

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** T111, T112, T121, T123 y T124 · **Responsable de la tarjeta:** Estiven Alexander Rodríguez Pérez · **Revisa:** Felix José Navarro Ortiz
- **Qué se pidió:** la prueba de datos personales, la prueba de rendimiento, la prueba de trazabilidad, la matriz completa y los documentos de tratamiento de datos y de release.
- **Archivos generados:** DatosPersonalesTest, TrazabilidadTest, RendimientoEstadoDeCuentaTest, docs/trazabilidad.md, docs/tratamiento-de-datos.md, docs/release.md, specs/001-habitagest/tasks.md
- **Qué se aceptó:** los archivos generados, con la lectura completa del diff por el revisor.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** `mvn verify` de web y de ms-finanzas; el revisor comprueba la matriz contra las clases de prueba existentes.
- **Ajustes tras la verificación:** ninguno registrado.
