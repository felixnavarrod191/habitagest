# Acuerdos del equipo

Versión 1.0 · 2026-11-07

## Definition of Ready

1. Tiene una intención clara y su valor (“para…”). Verifica: el equipo.
2. Tiene al menos 2 criterios de aceptación gruesos, uno de borde o de error. Verifica: el equipo y el responsable de calidad.
3. Es lo bastante pequeño para terminarse en un sprint y está estimado. Verifica: Developers.
4. Las dependencias están identificadas y ninguna bloquea desde fuera del equipo. Verifica: Scrum Master.
5. Se sabe a qué interesado preguntar para aclarar las reglas de negocio. Verifica: el equipo.
6. No exige la spec detallada: esa se redacta al tomar el ítem dentro del sprint. Verifica: todos.

## Definition of Done

1. La spec del ítem fue revisada y aprobada en su puerta. Se verifica con el acta de la puerta.
2. Cada criterio de aceptación tiene al menos una prueba automatizada, incluidos los casos de borde. Se verifica con la matriz de trazabilidad.
3. Las pruebas se escribieron antes del código y están en verde. Se verifica con el historial del repositorio.
4. El código llegó a la rama principal por un pull request que referencia una tarea (T###) y lo revisó otra persona.
5. El diff no incluye funcionalidad ni dependencias que ningún requisito pidió, o están justificadas en un ADR.
6. Todo cambio de estado de cuotas, reservas, PQR y órdenes queda auditado. Se verifica con una prueba de auditoría.
7. Sin hallazgos críticos en el análisis estático y sin secretos en el repositorio.
8. Los datos personales se tratan según la Ley 1581 de 2012 y los datos de prueba son ficticios.
9. El contrato OpenAPI (Swagger) está actualizado y las pruebas de contrato pasan.
10. La matriz de trazabilidad incluye el ítem: requisito, criterio y prueba.
11. El uso de IA quedó anotado en la bitácora.
12. Está desplegado en el entorno de pruebas y se puede demostrar en la Review.

## Working agreement

| Tema | Acuerdo |
|---|---|
| Horario | Sesiones con el docente los lunes y martes; el resto del trabajo es asíncrono y se coordina por el canal del equipo. Las reuniones empiezan y terminan a tiempo. |
| Comunicación | Las dudas de negocio se preguntan por el canal del equipo, no por mensajes privados; los acuerdos se escriben en la spec. |
| Código | Nada llega a la rama principal sin pull request, tarea asociada (T###) y revisión de otra persona. Pruebas antes que código. |
| IA | Se usa con instrucciones claras y nunca con datos reales; se revisa el diff completo; quien dirigió a la IA en una tarjeta no revisa su diff; se anota en la bitácora. |
| Impedimentos | Se avisan al Scrum Master apenas aparecen, sin esperar a la siguiente sesión, por el canal del equipo. |
| Tablero | Se actualiza antes de cada sesión; nadie mueve su propia tarjeta a Done. Máximo 3 tarjetas en Doing. |
| Respeto | Se critica el trabajo, no a la persona; las discrepancias técnicas se resuelven con un ADR, no con jerarquía. |
