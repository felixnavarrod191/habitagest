# Acta del Sprint Planning del Sprint 4

- **Fecha:** 24 de noviembre de 2026 (martes, tras el cierre del Sprint 3)
- **Facilita:** Angel Daniel Moreno Lazcano · **Revisa el acta:** Felix José Navarro Ortiz
- **Participantes:** Felix José Navarro Ortiz, Angel Daniel Moreno Lazcano y Estiven Alexander Rodríguez Pérez

## Sprint Goal

Al terminar el sprint, HabitaGest está desplegado en la nube con sus 11 roles, el Consejo y el Revisor fiscal consultan el resumen y las observaciones, y el equipo tiene la matriz de trazabilidad completa para presentar el release.

## Compromiso (23 puntos)

| Ítem | Puntos | Contenido |
|---|---|---|
| PBI-12 | 5 | Resumen de cartera y observaciones de auditoría |
| PBI-13 | 13 | Despliegue de los servicios en Render |
| PBI-14 | 5 | Calidad, trazabilidad final y preparación del release |


## Responsables y revisores

| Tarjeta | Responsable | Revisa | Fechas |
|---|---|---|---|
| Sprint Planning del Sprint 4 y specs de sus PBI | Angel Daniel Moreno Lazcano | Felix José Navarro Ortiz | 24 al 25 de noviembre |
| PBI-12 Resumen de cartera y observaciones de auditoría (T107 a T110) | Angel Daniel Moreno Lazcano | Estiven Alexander Rodríguez Pérez | 25 al 27 de noviembre |
| PBI-13 Despliegue de los servicios en Render (T113 a T120 y T122) | Felix José Navarro Ortiz | Angel Daniel Moreno Lazcano | 26 al 29 de noviembre |
| PBI-14 Calidad, trazabilidad final y preparación del release (T111, T112, T121, T123 y T124) | Estiven Alexander Rodríguez Pérez | Felix José Navarro Ortiz | 28 al 30 de noviembre |
| Review final y cierre del proyecto | Angel Daniel Moreno Lazcano | Felix José Navarro Ortiz | 30 de noviembre |

## Decisiones del Planning

- El resumen de cartera y gastos (REQ-PH-043) lo entrega ms-finanzas y consulta los gastos a ms-mantenimiento por REST: ADR-006.
- El resumen de un mes suma el saldo pendiente de las cuotas de ese mes (incluye la mora) y los gastos de las órdenes de mantenimiento completadas ese mes.
- Las observaciones de auditoría (REQ-PH-044) las deja solo el revisor fiscal; las consultan el revisor fiscal y el administrador del conjunto. Es la única escritura que el revisor fiscal tiene permitida (REQ-PH-017 sigue vigente para todo lo demás).
- El despliegue usa un servicio de Render por módulo, descrito en `render.yaml`, y una cuenta de MongoDB Atlas con una base por servicio (ADR-005).
- La prueba de contrato (NFR-PH-006) compara las operaciones de cada contrato OpenAPI con los controladores de su servicio.
- Los datos personales se revisan con una prueba automática antes de cada entrega (REQ-PH-045, NFR-PH-004).
- Las pantallas web del resumen y de las observaciones se entregan con PBI-12 como parte de la modificación aparte de pantallas por rol, sin tarea T### ni puntos.

## Acuerdos

- Definition of Ready, Definition of Done y working agreement: ver `docs/acuerdos.md`.
- Sombreros del Sprint 4: facilitador del Daily, Felix José Navarro Ortiz; guardián de la DoD, Angel Daniel Moreno Lazcano; revisor del diff de la IA, Estiven Alexander Rodríguez Pérez, que nunca revisa una tarjeta que él dirigió (por eso PBI-14 la revisa Felix José Navarro Ortiz); responsable de calidad y trazabilidad, Estiven Alexander Rodríguez Pérez.
- Nadie comprueba la DoD de su propia tarjeta: la de PBI-12 y la de la Review final (de Angel Daniel Moreno Lazcano) las comprueba Estiven Alexander Rodríguez Pérez; la de PBI-13 y la de PBI-14 las comprueba Angel Daniel Moreno Lazcano; la del Planning la comprueba Felix José Navarro Ortiz.
- Las puertas y las revisiones se hacen por escrito en el pull request, sin reunión.
- Orden de fusión: PBI-12, PBI-13 y, al final, PBI-14, que marca todas las tareas como hechas y completa la matriz de trazabilidad.
- Daily: en cada sesión con el docente; los demás días, por escrito en el canal del equipo.
- Máximo 3 tarjetas en Doing.
