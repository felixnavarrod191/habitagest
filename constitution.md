# Constitución de HabitaGest

Versión 1.0 · Fecha: 2026-11-07 · Se ratifica en la Puerta 0 (docs/actas/puerta-0.md)

## Propósito

Estas son las reglas que gobiernan todas las decisiones del proyecto, sin importar la funcionalidad. Sirven de freno contra la sobre-ingeniería y contra los atajos, incluidos los que toma la IA. Si una decisión las contradice, se corrige la decisión.

## Principios no negociables

### 1. Ninguna funcionalidad sin criterios de aceptación aprobados

- **Qué exige:** Ninguna funcionalidad sin criterios de aceptación aprobados.
- **Qué evita:** Código sin forma de verificarse.
- **Cómo se verifica:** Matriz de trazabilidad: cada requisito tiene al menos un criterio; lo revisa la Puerta 1.

### 2. Las pruebas se escriben antes del código, derivadas de los criterios

- **Qué exige:** Las pruebas se escriben antes del código, derivadas de los criterios.
- **Qué evita:** Pruebas que se adaptan al código en lugar de a la especificación.
- **Cómo se verifica:** Historial del repositorio: el commit de la prueba va antes que el de la implementación de la misma tarea (DoD 3).

### 3. Microservicios con una base de datos por servicio y comunicación solo por REST documentada en OpenAPI

- **Qué exige:** Microservicios con una base de datos por servicio y comunicación solo por REST documentada en OpenAPI.
- **Qué evita:** Acoplamiento oculto y dependencias entre bases de datos.
- **Cómo se verifica:** ADR-001, ADR-002 y ADR-004; búsqueda en el código: ningún servicio usa la cadena de conexión de otro.

### 4. Seguridad por defecto y mínimo privilegio por rol; sin secretos en el repositorio

- **Qué exige:** Seguridad por defecto y mínimo privilegio por rol; sin secretos en el repositorio.
- **Qué evita:** Accesos indebidos y fugas de contraseñas.
- **Cómo se verifica:** Prueba de acceso por cada uno de los 11 roles (NFR-PH-002) y búsqueda de cadenas de conexión de Atlas y de contraseñas en el repositorio: cero resultados.

### 5. Todo cambio de estado de cuotas, reservas, PQR y órdenes queda auditado (quién y cuándo)

- **Qué exige:** Todo cambio de estado de cuotas, reservas, PQR y órdenes queda auditado (quién y cuándo).
- **Qué evita:** Pérdida de trazabilidad del negocio.
- **Cómo se verifica:** Prueba de auditoría de PQR y órdenes (NFR-PH-003).

### 6. Los datos personales se tratan según la Ley 1581 de 2012; las pruebas usan datos ficticios

- **Qué exige:** Los datos personales se tratan según la Ley 1581 de 2012; las pruebas usan datos ficticios.
- **Qué evita:** Incumplimiento legal y fuga de datos de residentes.
- **Cómo se verifica:** Lista de comprobación de la Ley 1581 en cada pull request y revisión de los datos de prueba (NFR-PH-004).

### 7. Todo pull request referencia una tarea (T###) y su diff lo revisa otra persona, incluido el código generado por IA

- **Qué exige:** Todo pull request referencia una tarea (T###) y su diff lo revisa otra persona, incluido el código generado por IA.
- **Qué evita:** Cambios que no se pueden rastrear a un requisito.
- **Cómo se verifica:** Cada pull request cita una tarea (T###), lo revisa una persona distinta y, si hubo IA, tiene entrada en docs/ia/bitacora.md.

## Gobernanza

- La constitución solo cambia con un ADR aprobado por los tres integrantes del equipo.
- Cada cambio se anota en el historial con su versión, su fecha y el ADR que lo origina.
- Cuando una tarea o un pull request entra en conflicto con un principio, gana el principio.

## Historial

| Versión | Fecha | Cambio |
|---|---|---|
| 1.0 | 2026-11-07 | Versión inicial |
