# Preparación del release

Versión 1.0 · 2026-11-26 · HU-34, NFR-PH-007. Un requisito comprometido sin criterio, tarea o prueba bloquea el release.

## 1. Lo que bloquea el release

| Control | Cómo se comprueba | Responsable |
|---|---|---|
| Los 45 requisitos funcionales y los 7 no funcionales tienen criterio, tarea y prueba | `TrazabilidadTest` (módulo web) y `docs/trazabilidad.md` | Estiven Alexander Rodríguez Pérez |
| Todas las tareas de `specs/001-habitagest/tasks.md` (T001 a T124) están hechas | `TrazabilidadTest` | Estiven Alexander Rodríguez Pérez |
| Cada contrato OpenAPI coincide con su servicio | `ContratoOpenApiTest` en los 6 servicios | Felix José Navarro Ortiz |
| Sin datos personales reales en el repositorio | `DatosPersonalesTest` | Estiven Alexander Rodríguez Pérez |
| Sin `MONGODB_URI` el módulo no arranca y lo informa | `ConfiguracionObligatoriaTest` en cada módulo | Felix José Navarro Ortiz |
| El estado de cuenta responde en menos de 1 segundo con 120 apartamentos | `RendimientoEstadoDeCuentaTest` | Estiven Alexander Rodríguez Pérez |
| Los 8 módulos responden en Render | `verificar-despliegue.ps1` y `docs/despliegue-render.md` | Felix José Navarro Ortiz |

## 2. Lista de comprobación del release

1. `main` está actualizada y sin cambios locales (`git status` vacío).
2. `mvn verify` termina en `BUILD SUCCESS` en cada uno de los 8 módulos.
3. `docs/trazabilidad.md` no tiene filas con `—` en la columna Prueba.
4. `docs/ia/bitacora-sprint-4.md` tiene una entrada por cada tarjeta del Sprint 4.
5. Los 8 servicios de Render responden en `/actuator/health` y los 6 servicios de negocio muestran su `/swagger-ui.html`.
6. Se recorrieron los 11 roles en la web desplegada con los usuarios de demostración.
7. La Review final se presenta con el sistema desplegado, el burn down del Sprint 4 y el burn up del proyecto.

## 3. Alcance entregado

34 historias comprometidas (HU-01 a HU-34) en los PBI-01 a PBI-14.
