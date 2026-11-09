# tasks.md · HabitaGest

Versión 1.0 · 2026-11-07 · Se aprueba en la Puerta 3 (`docs/actas/puerta-3.md`)

Convenciones: `[P]` = se puede hacer en paralelo · `[USn]` = historia HU-n · entre paréntesis, los requisitos y criterios que cubre. En cada fase, la prueba va antes que la implementación. Cada tarea dura como máximo medio día y toca un solo servicio.

## Fase 0 · Preparación (Sprint 1)

- [ ] T001 [P] Prueba de humo de cada módulo: sin `MONGODB_URI` el servicio no arranca y lo informa (NFR-PH-005)
- [ ] T002 Crear el repositorio `habitagest`, proteger `main`, estructura de carpetas, `.gitignore` y README (constitución, principio 4)
- [ ] T003 [P] Crear el clúster de Atlas, el usuario y las 7 bases (copropiedad_db, finanzas_db, reservas_db, pqr_db, porteria_db, mantenimiento_db y web_db); la conexión solo en `MONGODB_URI`
- [ ] T004 [P] Crear el módulo ms-copropiedad con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-copropiedad
- [ ] T005 [P] Crear el módulo ms-finanzas con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-finanzas
- [ ] T006 [P] Crear el módulo ms-reservas con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-reservas
- [ ] T007 [P] Crear el módulo ms-pqr con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-pqr
- [ ] T008 [P] Crear el módulo ms-porteria con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-porteria
- [ ] T009 [P] Crear el módulo ms-mantenimiento con health, Swagger y validación de configuración (NFR-PH-005, NFR-PH-006) · ms-mantenimiento
- [ ] T010 [P] Crear el módulo api-gateway con las rutas a los 6 servicios por variables de entorno · api-gateway
- [ ] T011 [P] Crear el módulo web con Thymeleaf, Bootstrap y Spring Security base · web
- [ ] T012 [P] Dockerfile de dos etapas y .dockerignore en cada módulo (NFR-PH-005)
- [ ] T013 Verificar `mvn verify` y el arranque local de los 8 módulos; guardar la evidencia de health en UP

Punto de control: los 8 módulos compilan, arrancan y no hay secretos en el repositorio.

## Fase 1 · Fundamentos (bloquea las demás)

- [ ] T014 [P] [US2] Prueba: AC-001.1 (REQ-PH-001) · web
- [ ] T015 [US2] Mostrar a cada usuario solo las opciones de su rol (REQ-PH-001) · web
- [ ] T016 [P] [US2] Prueba: AC-003.1 (REQ-PH-003) · web
- [ ] T017 [US2] Rechazar el acceso a pantallas de otro rol (REQ-PH-003) · web
- [ ] T018 [P] [US1] Prueba: AC-002.1 (REQ-PH-002) · web
- [ ] T019 [US1] Crear usuarios con rol y contraseña cifrada (REQ-PH-002) · web
- [ ] T020 [US1] Enum de los 11 roles, entidad Usuario y repositorio sobre web_db (REQ-PH-002) · web
- [ ] T021 [US2] Configurar Spring Security: formulario de inicio de sesión, BCrypt, rutas por rol y página 403 (REQ-PH-001, REQ-PH-003) · web
- [ ] T022 [US2] Pantallas de inicio de sesión, inicio con menú por rol y 403 (REQ-PH-001) · web
- [ ] T023 [US1] Pantalla de usuarios del administrador del sistema (REQ-PH-002) · web
- [ ] T024 Datos de arranque: administrador desde `ADMIN_PASSWORD` y usuarios de demostración con `APP_DEMO=true` (NFR-PH-004) · web
- [ ] T025 [P] Prueba por tabla de los 11 roles: cada uno entra a su área y es rechazado en las demás (NFR-PH-002) · web

Punto de control (Sprint 1): inicio de sesión de los 11 roles probado.

Tareas de la fase 1 que se hacen en el Sprint 2:

- [ ] T026 Componente común de auditoría de cambios de estado (usuario y fecha-hora) (NFR-PH-003)
- [ ] T027 [P] Manejo de errores común con cuerpo `{codigo, mensaje}` en los 6 servicios
- [ ] T028 Completar las rutas del gateway y su prueba de enrutamiento · api-gateway

## Fase 2 · Copropiedad y cuotas (Sprint 2)

- [ ] T029 [P] [US3] Prueba: AC-004.1 (REQ-PH-004) · ms-copropiedad
- [ ] T030 [US3] Registrar apartamentos con torre, número y coeficiente (REQ-PH-004) · ms-copropiedad
- [ ] T031 [P] [US3] Prueba: AC-005.1 (REQ-PH-005) · ms-copropiedad
- [ ] T032 [US3] Rechazar coeficientes que superen el 100 % (REQ-PH-005) · ms-copropiedad
- [ ] T033 [P] [US4] Prueba: AC-006.1 (REQ-PH-006) · ms-copropiedad
- [ ] T034 [US4] Registrar propietarios y asociarlos a su apartamento (REQ-PH-006) · ms-copropiedad
- [ ] T035 [P] [US5] Prueba: AC-007.1 (REQ-PH-007) · ms-copropiedad
- [ ] T036 [US5] Registrar residentes de un apartamento (REQ-PH-007) · ms-copropiedad
- [ ] T037 [P] [US6] Prueba: AC-008.1 (REQ-PH-008) · ms-finanzas
- [ ] T038 [US6] Guardar el presupuesto mensual (REQ-PH-008) · ms-finanzas
- [ ] T039 [P] [US7] Prueba: AC-009.1, AC-009.2 (REQ-PH-009) · ms-finanzas
- [ ] T040 [US7] Calcular y generar las cuotas del mes (REQ-PH-009) · ms-finanzas
- [ ] T041 [P] [US7] Prueba: AC-010.1 (REQ-PH-010) · ms-finanzas
- [ ] T042 [US7] No generar cuotas sin presupuesto (REQ-PH-010) · ms-finanzas

Punto de control: todas las pruebas de la fase en verde y los contratos actualizados.

## Fase 3 · Pagos, mora y reservas (Sprint 2)

- [ ] T043 [P] [US8] Prueba: AC-011.1 (REQ-PH-011) · ms-finanzas
- [ ] T044 [US8] Registrar pagos y descontarlos del saldo (REQ-PH-011) · ms-finanzas
- [ ] T045 [P] [US8] Prueba: AC-012.1 (REQ-PH-012) · ms-finanzas
- [ ] T046 [US8] Rechazar pagos mayores al saldo (REQ-PH-012) · ms-finanzas
- [ ] T047 [P] [US9] Prueba: AC-013.1 (REQ-PH-013) · ms-finanzas
- [ ] T048 [US9] Aplicar el interés de mora (REQ-PH-013) · ms-finanzas
- [ ] T049 [P] [US11] Prueba: AC-014.1 (REQ-PH-014) · ms-finanzas
- [ ] T050 [US11] Marcar apartamentos en mora (REQ-PH-014) · ms-finanzas
- [ ] T051 [P] [US10] Prueba: AC-015.1 (REQ-PH-015) · ms-finanzas
- [ ] T052 [US10] Estado de cuenta del propietario (REQ-PH-015) · ms-finanzas
- [ ] T053 [P] [US11] Prueba: AC-016.1 (REQ-PH-016) · ms-finanzas
- [ ] T054 [US11] Cartera filtrada por torre (REQ-PH-016) · ms-finanzas
- [ ] T055 [P] [US12] Prueba: AC-017.1 (REQ-PH-017) · ms-finanzas
- [ ] T056 [US12] Consulta de solo lectura del revisor fiscal (REQ-PH-017) · ms-finanzas
- [ ] T057 [P] [US13] Prueba: AC-018.1 (REQ-PH-018) · ms-reservas
- [ ] T058 [US13] Definir zonas comunes con su horario (REQ-PH-018) · ms-reservas
- [ ] T059 [P] [US14] Prueba: AC-019.1 (REQ-PH-019) · ms-reservas
- [ ] T060 [US14] Consultar la disponibilidad de una zona (REQ-PH-019) · ms-reservas
- [ ] T061 [P] [US15] Prueba: AC-020.1 (REQ-PH-020) · ms-reservas
- [ ] T062 [US15] Confirmar reservas en horario libre (REQ-PH-020) · ms-reservas
- [ ] T063 [P] [US15] Prueba: AC-021.1 (REQ-PH-021) · ms-reservas
- [ ] T064 [US15] Rechazar reservas con cruce de horario (REQ-PH-021) · ms-reservas
- [ ] T065 [P] [US15] Prueba: AC-022.1, AC-022.2 (REQ-PH-022) · ms-reservas
- [ ] T066 [US15] Rechazar reservas por mora, máximo mensual o duración (REQ-PH-022) · ms-reservas
- [ ] T067 [P] [US16] Prueba: AC-023.1 (REQ-PH-023) · ms-reservas
- [ ] T068 [US16] Cancelar reservas con 24 horas de anticipación (REQ-PH-023) · ms-reservas
- [ ] T069 [P] [US16] Prueba: AC-024.1 (REQ-PH-024) · ms-reservas
- [ ] T070 [US16] Rechazar cancelaciones con menos de 24 horas (REQ-PH-024) · ms-reservas

Punto de control: todas las pruebas de la fase en verde y los contratos actualizados.

## Fase 4 · Convivencia y operación (Sprint 3)

- [ ] T071 [P] [US17] Prueba: AC-025.1 (REQ-PH-025) · ms-pqr
- [ ] T072 [US17] Radicar PQR en estado Radicada (REQ-PH-025) · ms-pqr
- [ ] T073 [P] [US18] Prueba: AC-026.1 (REQ-PH-026) · ms-pqr
- [ ] T074 [US18] Pasar la PQR al estado siguiente con historial (REQ-PH-026) · ms-pqr
- [ ] T075 [P] [US18] Prueba: AC-027.1 (REQ-PH-027) · ms-pqr
- [ ] T076 [US18] Rechazar saltos de estado de la PQR (REQ-PH-027) · ms-pqr
- [ ] T077 [P] [US19] Prueba: AC-028.1 (REQ-PH-028) · ms-pqr
- [ ] T078 [US19] Mostrar a cada residente solo sus PQR (REQ-PH-028) · ms-pqr
- [ ] T079 [P] [US20] Prueba: AC-029.1 (REQ-PH-029) · ms-pqr
- [ ] T080 [US20] Cerrar PQR resueltas con respuesta (REQ-PH-029) · ms-pqr
- [ ] T081 [P] [US18] Prueba: AC-030.1 (REQ-PH-030) · ms-pqr y ms-mantenimiento
- [ ] T082 [US18] Auditar los cambios de estado de PQR y órdenes (REQ-PH-030) · ms-pqr y ms-mantenimiento
- [ ] T083 [P] [US21] Prueba: AC-031.1 (REQ-PH-031) · ms-pqr
- [ ] T084 [US21] Publicar y mostrar comunicados vigentes (REQ-PH-031) · ms-pqr
- [ ] T085 [P] [US22] Prueba: AC-032.1 (REQ-PH-032) · ms-pqr
- [ ] T086 [US22] Ocultar comunicados vencidos (REQ-PH-032) · ms-pqr
- [ ] T087 [P] [US23] Prueba: AC-033.1 (REQ-PH-033) · ms-porteria
- [ ] T088 [US23] Autorizar visitas (REQ-PH-033) · ms-porteria
- [ ] T089 [P] [US24] Prueba: AC-034.1 (REQ-PH-034) · ms-porteria
- [ ] T090 [US24] Registrar ingreso y salida de visitantes (REQ-PH-034) · ms-porteria
- [ ] T091 [P] [US24] Prueba: AC-035.1 (REQ-PH-035) · ms-porteria
- [ ] T092 [US24] Rechazar visitantes no autorizados (REQ-PH-035) · ms-porteria
- [ ] T093 [P] [US25] Prueba: AC-036.1 (REQ-PH-036) · ms-porteria
- [ ] T094 [US25] Registrar paquetes y mostrarlos al residente (REQ-PH-036) · ms-porteria
- [ ] T095 [P] [US26] Prueba: AC-037.1 (REQ-PH-037) · ms-porteria
- [ ] T096 [US26] Registrar novedades de seguridad (REQ-PH-037) · ms-porteria
- [ ] T097 [P] [US27] Prueba: AC-038.1 (REQ-PH-038) · ms-mantenimiento
- [ ] T098 [US27] Crear órdenes de mantenimiento (REQ-PH-038) · ms-mantenimiento
- [ ] T099 [P] [US27] Prueba: AC-039.1 (REQ-PH-039) · ms-mantenimiento
- [ ] T100 [US27] Aprobar órdenes bajo el tope (REQ-PH-039) · ms-mantenimiento
- [ ] T101 [P] [US28] Prueba: AC-040.1 (REQ-PH-040) · ms-mantenimiento
- [ ] T102 [US28] Exigir al Consejo la aprobación sobre el tope (REQ-PH-040) · ms-mantenimiento
- [ ] T103 [P] [US29] Prueba: AC-041.1 (REQ-PH-041) · ms-mantenimiento
- [ ] T104 [US29] Ejecutar órdenes y registrar materiales (REQ-PH-041) · ms-mantenimiento
- [ ] T105 [P] [US30] Prueba: AC-042.1 (REQ-PH-042) · ms-mantenimiento
- [ ] T106 [US30] Mostrar al proveedor solo sus órdenes (REQ-PH-042) · ms-mantenimiento

Punto de control: todas las pruebas de la fase en verde y los contratos actualizados.

## Fase 5 · Cierre (Sprint 4)

- [ ] T107 [P] [US31] Prueba: AC-043.1 (REQ-PH-043) · ms-finanzas
- [ ] T108 [US31] Resumen de cartera y gastos para el Consejo (REQ-PH-043) · ms-finanzas
- [ ] T109 [P] [US32] Prueba: AC-044.1 (REQ-PH-044) · ms-finanzas
- [ ] T110 [US32] Observaciones de auditoría del revisor fiscal (REQ-PH-044) · ms-finanzas
- [ ] T111 [P] [US5] Prueba: AC-045.1 (REQ-PH-045) · transversal
- [ ] T112 [US5] Verificar el tratamiento de datos personales (REQ-PH-045) · transversal

Punto de control: todas las pruebas de la fase en verde y los contratos actualizados.

### Despliegue y cierre (Sprint 4)

- [ ] T113 [P] [US33] Servicio de Render para ms-copropiedad con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-copropiedad
- [ ] T114 [P] [US33] Servicio de Render para ms-finanzas con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-finanzas
- [ ] T115 [P] [US33] Servicio de Render para ms-reservas con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-reservas
- [ ] T116 [P] [US33] Servicio de Render para ms-pqr con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-pqr
- [ ] T117 [P] [US33] Servicio de Render para ms-porteria con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-porteria
- [ ] T118 [P] [US33] Servicio de Render para ms-mantenimiento con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · ms-mantenimiento
- [ ] T119 [P] [US33] Servicio de Render para api-gateway con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · api-gateway
- [ ] T120 [P] [US33] Servicio de Render para web con su Dockerfile y sus variables de entorno (HU-33, NFR-PH-005) · web
- [ ] T121 [P] [US10] Prueba de rendimiento del estado de cuenta con 120 apartamentos (NFR-PH-001) · ms-finanzas
- [ ] T122 [P] [US33] Pruebas de contrato de cada servicio contra su OpenAPI (NFR-PH-006)
- [ ] T123 [US34] Completar la matriz de trazabilidad: requisito, criterio, tarea y prueba (NFR-PH-007)
- [ ] T124 [US34] Preparar el release: revisar que ningún requisito comprometido quede sin prueba (HU-34)

Punto de control final: el 100 % de los requisitos comprometidos tiene criterio, tarea y prueba.
