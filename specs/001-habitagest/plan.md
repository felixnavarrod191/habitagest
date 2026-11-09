# Plan técnico · Spec 001 · HabitaGest

Versión 1.0 · 2026-11-08 · Se aprueba en la Puerta 2 (`docs/actas/puerta-2.md`). Cada decisión responde a un requisito (REQ) o a una restricción de la spec.

## 1. Contexto técnico

- Java 17 y Spring Boot 3.x; Spring Data MongoDB; Spring Security con contraseñas cifradas con BCrypt (REQ-PH-001 a 003, NFR-PH-002).
- Interfaz web con Thymeleaf y Bootstrap, que consume los servicios a través del API Gateway.
- MongoDB Atlas con una base de datos por servicio, sin compartir bases entre servicios (constitución, principio 3; ADR-001).
- Contratos OpenAPI (Swagger) escritos antes que el código, uno por servicio (NFR-PH-006; ADR-004).
- Docker y despliegue en Render; la conexión a la base de datos solo por la variable `MONGODB_URI` (NFR-PH-005; ADR-005).

## 2. Arquitectura y componentes

| Módulo | Responsabilidad | Base de datos | Puerto local | Requisitos |
|---|---|---|---|---|
| ms-copropiedad | Torres, apartamentos con coeficiente, propietarios y residentes | copropiedad_db | 8081 | REQ-PH-004 a 007 |
| ms-finanzas | Presupuesto, cuotas, pagos, mora, cartera, estado de cuenta, resumen y observaciones | finanzas_db | 8082 | REQ-PH-008 a 017, REQ-PH-043, REQ-PH-044 |
| ms-reservas | Zonas comunes, disponibilidad y reservas | reservas_db | 8083 | REQ-PH-018 a 024 |
| ms-pqr | PQR con su flujo de estados y comunicados | pqr_db | 8084 | REQ-PH-025 a 029, REQ-PH-031 a 032 |
| ms-porteria | Visitas, ingresos, paquetes y novedades | porteria_db | 8085 | REQ-PH-033 a 037 |
| ms-mantenimiento | Órdenes, aprobación del Consejo, materiales y proveedores | mantenimiento_db | 8086 | REQ-PH-038 a 042 |
| api-gateway | Punto único de entrada que enruta a cada servicio | Ninguna | 8090 | Transversal |
| web | Interfaz por rol, inicio de sesión, usuarios y roles | web_db | 8080 | REQ-PH-001 a 003 |

El requisito REQ-PH-030 (auditoría) lo cumplen ms-pqr y ms-mantenimiento con un componente común. REQ-PH-045 y NFR-PH-004 son transversales.

**Regla de dependencia:** un servicio nunca lee la base de otro; si necesita un dato ajeno lo pide por REST al servicio dueño.

| Quien llama | Servicio al que llama | Para qué | Requisito |
|---|---|---|---|
| ms-reservas | ms-finanzas | Saber si el apartamento está en mora antes de reservar | REQ-PH-022 |
| ms-reservas | ms-copropiedad | Validar que el apartamento y quien reserva existen | REQ-PH-020 |
| ms-finanzas | ms-copropiedad | Obtener los coeficientes al generar las cuotas | REQ-PH-009 |
| ms-porteria | ms-copropiedad | Validar el apartamento de una visita | REQ-PH-033 |
| web | api-gateway | Todas las operaciones de negocio | Todos |

## 3. Modelo de datos por servicio

| Servicio | Colecciones (campos principales) | Índices |
|---|---|---|
| ms-copropiedad | torres (nombre) · apartamentos (torre, numero, coeficiente, propietarioId) · propietarios (nombre, documento) · residentes (nombre, documento, apartamentoId) | apartamentos (torre, numero) único |
| ms-finanzas | presupuestos (mes, valor) · cuotas (apartamentoId, mes, valor, saldo, mora, estado) · pagos (cuotaId, valor, fecha, registradoPor) · observaciones (referencia, texto, autor, fecha) | presupuestos (mes) único · cuotas (apartamentoId, mes) único |
| ms-reservas | zonas (nombre, horaInicio, horaFin) · reservas (zonaId, apartamentoId, inicio, fin, estado) | reservas (zonaId, inicio) |
| ms-pqr | pqr (numero, residenteId, asunto, descripcion, estado, historial[]) · comunicados (titulo, texto, vigenteHasta) | pqr (numero) único · pqr (residenteId) |
| ms-porteria | visitas (apartamentoId, nombre, documento, fecha, autorizadaPor) · ingresos (visitaId, entrada, salida, registradoPor) · paquetes (apartamentoId, descripcion, estado) · novedades (texto, fecha, autor) | visitas (fecha) |
| ms-mantenimiento | ordenes (descripcion, zona, costoEstimado, estado, proveedorId, materiales[], costoFinal, historial[]) | ordenes (estado) · ordenes (proveedorId) |
| web | usuarios (usuario, passwordHash, rol, activo, apartamentoId) | usuarios (usuario) único |

Los servicios se refieren a datos de otros por identificador, sin uniones entre bases.

## 4. Seguridad

- Autenticación y autorización por rol en la aplicación web: los 11 roles técnicos son ADMIN_SISTEMA, ADMIN_CONJUNTO, CONSEJO, PROPIETARIO, RESIDENTE, PORTERO, VIGILANTE, CONTADOR, REVISOR_FISCAL, ENCARGADO_MANTENIMIENTO y PROVEEDOR (REQ-PH-001, REQ-PH-003).
- Contraseñas con BCrypt; la contraseña del administrador inicial viene de la variable `ADMIN_PASSWORD` y nunca del código (REQ-PH-002; constitución, principio 4).
- Una ruta de otro rol devuelve la página 403 (REQ-PH-003).
- Propietarios y residentes ven solo los datos de su apartamento (REQ-PH-006, REQ-PH-028).

## 5. Auditoría

Un componente común de cada servicio registra usuario, fecha-hora, estado anterior y estado nuevo en cada cambio de estado de PQR y de órdenes de mantenimiento (REQ-PH-030, NFR-PH-003).

## 6. Estrategia de pruebas

- Pruebas unitarias de las reglas de negocio (RN-01 a RN-12), escritas antes del código a partir de los criterios de aceptación.
- Pruebas de contrato de cada servicio contra su OpenAPI (NFR-PH-006).
- Pruebas de integración de los flujos entre servicios (por ejemplo, reserva con consulta de mora).
- Una prueba de acceso por cada uno de los 11 roles (NFR-PH-002).
- Prueba de humo de cada módulo: sin `MONGODB_URI` no arranca y lo informa (NFR-PH-005).

## 7. Despliegue

- Un servicio de Render por módulo (Docker, con su carpeta raíz) y un clúster de Atlas con una base de datos por servicio.
- Variables de entorno: `MONGODB_URI`, las direcciones de los servicios para el gateway (`COPROPIEDAD_URL`, `FINANZAS_URL`, `RESERVAS_URL`, `PQR_URL`, `PORTERIA_URL`, `MANTENIMIENTO_URL`) y, en la web, `GATEWAY_URL`, `ADMIN_PASSWORD` y `APP_DEMO`.
- Los servicios del plan gratuito se duermen tras un periodo sin uso.

## 8. Cobertura del plan frente a la spec

| Requisitos | Componente | Sprint |
|---|---|---|
| REQ-PH-001 a 003 (seguridad) | web y api-gateway | Sprint 1 |
| REQ-PH-004 a 007 (copropiedad) | ms-copropiedad | Sprint 2 |
| REQ-PH-008 a 017 (cuotas, pagos y mora) | ms-finanzas | Sprint 2 |
| REQ-PH-018 a 024 (reservas) | ms-reservas | Sprint 2 |
| REQ-PH-025 a 032 (PQR, auditoría y comunicados) | ms-pqr y ms-mantenimiento | Sprint 3 |
| REQ-PH-033 a 037 (portería y vigilancia) | ms-porteria | Sprint 3 |
| REQ-PH-038 a 042 (mantenimiento) | ms-mantenimiento | Sprint 3 |
| REQ-PH-043 a 044 (resumen y auditoría) | ms-finanzas y ms-mantenimiento | Sprint 4 |
| REQ-PH-045 y NFR-PH-004 | Transversal | Todos |

## 9. Decisiones de arquitectura

ADR-001 una base de datos por servicio · ADR-002 API Gateway como punto de entrada · ADR-003 autenticación con Spring Security y sesión en la web · ADR-004 contrato OpenAPI primero · ADR-005 despliegue con Docker en Render y Atlas. Detalle en `docs/adr/`.

## 10. Riesgos

- Despliegue de 8 módulos en el plan gratuito de Render (memoria y tiempo activo): se mitiga con la opción de memoria de la JVM y arrancando los servicios antes de cada demostración.
- Carga del equipo de 3 personas: el objetivo extendido absorbe la variación.
