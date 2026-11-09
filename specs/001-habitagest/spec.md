# Spec 001 · HabitaGest: gestión de un conjunto residencial

| Metadato | Valor |
|---|---|
| Versión | 1.0 |
| Estado | Aprobada (Puerta 1: `docs/actas/puerta-1.md`) |
| Autoría | Felix José Navarro Ortiz, Angel Daniel Moreno Lazcano y Estiven Alexander Rodríguez Pérez |
| Fecha | 2026-11-08 |

Describe el qué y el porqué, no el cómo. El cómo está en `plan.md`.

## 1. Contexto y problema

El Conjunto Residencial Torres del Mirador (3 torres, 120 apartamentos, ficticio) administra hoy sus cuotas y su mora en hojas sueltas, anota las reservas de zonas comunes en un cuaderno y recibe los reclamos de forma verbal. Eso produce reservas que se cruzan, cuotas calculadas a mano con errores, mora sin seguimiento y reclamos sin rastro. Lo sufren los propietarios y residentes, y también la administración, el contador y el Consejo, que no tienen cifras confiables.

## 2. Objetivos y no-objetivos

**Objetivos (medibles)**

- Cada cuota se calcula con la fórmula del coeficiente y la suma de las cuotas del mes iguala el presupuesto.
- Ninguna reserva se cruza con otra en la misma zona y horario.
- Cada PQR, pago y orden de mantenimiento queda registrado con quién lo hizo y cuándo.
- Cada usuario ve solo lo que corresponde a su rol y a su apartamento.
- El 100 % de los requisitos comprometidos tiene criterio, tarea y prueba (NFR-PH-007).

**No-objetivos**

- Pagos en línea reales ni pasarela de pagos.
- Facturación electrónica y contabilidad completa.
- Aplicación móvil.
- Control de acceso físico con hardware (torniquetes, cámaras).
- Cuotas extraordinarias y multas.

## 3. Actores

Los 11 roles del sistema: administrador del sistema, administrador del conjunto, Consejo de administración, propietario, residente, portero, vigilante, contador, revisor fiscal, encargado de mantenimiento y proveedor.

## 4. Historias de usuario

Cada historia sigue la plantilla: Como [rol] quiero [capacidad] para [beneficio]; escenario con datos; reglas; requisitos; criterios; fuera de alcance. Las historias HU-35 a HU-38 son el objetivo extendido (lista de reserva) y se especifican cuando se toman.

### HU-01 · Crear usuarios y asignarles uno de los 11 roles

Como administrador del sistema quiero crear usuarios y asignarles uno de los 11 roles para controlar quién accede al sistema y con qué permisos.

- **PBI-03** · Must · Sprint 1
- **Escenario:** El administrador del sistema crea el usuario «contador1» con el rol Contador y una contraseña inicial. Si intenta crear otro con el mismo nombre, el sistema lo rechaza.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-002
- **Criterios:** AC-002.1
- **Fuera de alcance:** recuperación de contraseña por correo.

### HU-02 · Iniciar sesión y ver solo las opciones de mi rol

Como usuario del sistema quiero iniciar sesión y ver solo las opciones de mi rol para trabajar de forma segura sin acceder a funciones ajenas.

- **PBI-03** · Must · Sprint 1
- **Escenario:** El portero inicia sesión y ve únicamente las opciones de portería. Si intenta abrir una pantalla del contador, el sistema le indica que no tiene permiso.
- **Reglas:** RN-11, RN-12
- **Requisitos:** REQ-PH-001, REQ-PH-003
- **Criterios:** AC-001.1, AC-003.1
- **Fuera de alcance:** inicio de sesión con redes sociales.

### HU-03 · Registrar las torres y los apartamentos con su coeficiente de copropiedad

Como administrador del conjunto quiero registrar las torres y los apartamentos con su coeficiente de copropiedad para tener la base para calcular las cuotas.

- **PBI-04** · Must · Sprint 2
- **Escenario:** El administrador registra la torre 1 y el apartamento 301 con coeficiente de 1,5 %. Si el coeficiente hiciera que la suma superara el 100 %, el sistema rechaza el registro e indica la suma resultante.
- **Reglas:** RN-02
- **Requisitos:** REQ-PH-004, REQ-PH-005
- **Criterios:** AC-004.1, AC-005.1
- **Fuera de alcance:** áreas en metros cuadrados y planos.

### HU-04 · Registrar a los propietarios y asociarlos a su apartamento

Como administrador del conjunto quiero registrar a los propietarios y asociarlos a su apartamento para saber a quién se cobra y quién puede consultar su estado de cuenta.

- **PBI-04** · Must · Sprint 2
- **Escenario:** El administrador registra a un propietario y lo asocia al 301. Al iniciar sesión, el propietario ve los datos del 301 y no los del 302. Cada apartamento tiene un solo propietario responsable del pago.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-006
- **Criterios:** AC-006.1
- **Fuera de alcance:** historial de cambios de propietario.

### HU-05 · Registrar a los residentes de cada apartamento

Como administrador del conjunto quiero registrar a los residentes de cada apartamento para controlar quién habita y a quién se puede autorizar.

- **PBI-04** · Must · Sprint 2
- **Escenario:** El administrador registra dos residentes en el 301 con datos ficticios, tratados según la Ley 1581.
- **Reglas:** RN-11, RN-12
- **Requisitos:** REQ-PH-007, REQ-PH-045
- **Criterios:** AC-007.1, AC-045.1
- **Fuera de alcance:** carnetización y fotografías.

### HU-06 · Definir el presupuesto mensual de administración

Como administrador del conjunto quiero definir el presupuesto mensual de administración para contar con el valor base de las cuotas.

- **PBI-05** · Must · Sprint 2
- **Escenario:** El administrador guarda el presupuesto de noviembre en $10.000.000 y queda registrado para ese mes.
- **Reglas:** RN-01
- **Requisitos:** REQ-PH-008
- **Criterios:** AC-008.1
- **Fuera de alcance:** presupuesto anual por rubros (objetivo extendido).

### HU-07 · Generar las cuotas del mes de todos los apartamentos según su coeficiente

Como administrador del conjunto quiero generar las cuotas del mes de todos los apartamentos según su coeficiente para cobrar a cada propietario un valor justo.

- **PBI-05** · Must · Sprint 2
- **Escenario:** El presupuesto de noviembre es de $10.000.000. El apartamento 301 tiene un coeficiente de 1,5 % y el 302 de 0,9 %. El administrador genera las cuotas: el 301 debe $150.000 y el 302 $90.000. Si olvida cargar el presupuesto, el sistema no genera nada y avisa el motivo.
- **Reglas:** RN-01, RN-02
- **Requisitos:** REQ-PH-009, REQ-PH-010
- **Criterios:** AC-009.1, AC-009.2, AC-010.1
- **Fuera de alcance:** cuotas extraordinarias y multas.

### HU-08 · Registrar el pago de una cuota

Como contador quiero registrar el pago de una cuota para mantener la cartera al día.

- **PBI-06** · Must · Sprint 2
- **Escenario:** El contador registra un pago de $100.000 a una cuota con saldo de $150.000 y el saldo queda en $50.000. Un pago de $200.000 se rechaza porque supera el saldo.
- **Reglas:** RN-04
- **Requisitos:** REQ-PH-011, REQ-PH-012
- **Criterios:** AC-011.1, AC-012.1
- **Fuera de alcance:** pagos en línea.

### HU-09 · Aplicar el interés de mora a las cuotas vencidas

Como contador quiero aplicar el interés de mora a las cuotas vencidas para cobrar lo que corresponde por los pagos atrasados.

- **PBI-06** · Must · Sprint 2
- **Escenario:** Una cuota de $150.000 venció el día 10. El contador aplica la mora y se suman $3.000 de interés (2 %).
- **Reglas:** RN-03
- **Requisitos:** REQ-PH-013
- **Criterios:** AC-013.1
- **Fuera de alcance:** interés compuesto y multas.

### HU-10 · Consultar mi estado de cuenta (cuotas, pagos y mora)

Como propietario quiero consultar mi estado de cuenta (cuotas, pagos y mora) para saber cuánto debo y desde cuándo.

- **PBI-06** · Must · Sprint 2
- **Escenario:** La propietaria del 301 consulta su estado de cuenta y ve una cuota vencida de $150.000, una mora de $3.000 y un saldo de $153.000.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-015
- **Criterios:** AC-015.1
- **Fuera de alcance:** descarga en PDF.

### HU-11 · Ver la cartera por torre y por apartamento

Como administrador del conjunto quiero ver la cartera por torre y por apartamento para identificar los apartamentos en mora.

- **PBI-06** · Must · Sprint 2
- **Escenario:** El administrador filtra la cartera por la torre 1 y ve solo sus apartamentos con su saldo; el 301 aparece en mora.
- **Reglas:** RN-03
- **Requisitos:** REQ-PH-014, REQ-PH-016
- **Criterios:** AC-014.1, AC-016.1
- **Fuera de alcance:** gráficos de cartera.

### HU-12 · Consultar en solo lectura las cuotas y los pagos

Como revisor fiscal quiero consultar en solo lectura las cuotas y los pagos para auditar la contabilidad del conjunto.

- **PBI-06** · Must · Sprint 2
- **Escenario:** El revisor fiscal consulta cuotas, pagos y órdenes de mantenimiento. Si intenta modificar un pago, el sistema lo impide.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-017
- **Criterios:** AC-017.1
- **Fuera de alcance:** dejar observaciones (HU-32).

### HU-13 · Definir las zonas comunes con su horario y sus reglas

Como administrador del conjunto quiero definir las zonas comunes con su horario y sus reglas para permitir reservas ordenadas.

- **PBI-07** · Must · Sprint 2
- **Escenario:** El administrador crea el salón social con horario de 8:00 a 22:00. La zona solo admite reservas dentro de ese horario.
- **Reglas:** RN-06
- **Requisitos:** REQ-PH-018
- **Criterios:** AC-018.1
- **Fuera de alcance:** cobro por el uso de la zona.

### HU-14 · Consultar la disponibilidad de una zona común

Como residente quiero consultar la disponibilidad de una zona común para elegir un horario libre.

- **PBI-07** · Must · Sprint 2
- **Escenario:** Un residente consulta el salón social del sábado y ve ocupado el horario de 14:00 a 16:00 y libres los demás.
- **Reglas:** RN-05
- **Requisitos:** REQ-PH-019
- **Criterios:** AC-019.1
- **Fuera de alcance:** vista de calendario mensual.

### HU-15 · Reservar una zona común

Como residente quiero reservar una zona común para usarla sin cruces con otros residentes.

- **PBI-07** · Must · Sprint 2
- **Escenario:** El residente del 301 reserva el salón social el sábado de 14:00 a 16:00. Si el residente del 305 intenta reservar de 15:00 a 17:00, el sistema lo rechaza por cruce. Si el 301 está en mora, o ya hizo 2 reservas ese mes, tampoco puede reservar.
- **Reglas:** RN-05, RN-06, RN-07
- **Requisitos:** REQ-PH-020, REQ-PH-021, REQ-PH-022
- **Criterios:** AC-020.1, AC-021.1, AC-022.1, AC-022.2
- **Fuera de alcance:** el cobro de la reserva.

### HU-16 · Cancelar mi reserva

Como residente quiero cancelar mi reserva para liberar el horario para otros residentes.

- **PBI-07** · Must · Sprint 2
- **Escenario:** El residente cancela el jueves su reserva del sábado a las 14:00 y el horario queda libre. Si intenta cancelarla el sábado a las 10:00, el sistema lo rechaza; el administrador sí puede cancelarla.
- **Reglas:** RN-05
- **Requisitos:** REQ-PH-023, REQ-PH-024
- **Criterios:** AC-023.1, AC-024.1
- **Fuera de alcance:** reprogramación automática.

### HU-17 · Radicar una petición, queja o reclamo (PQR)

Como residente quiero radicar una petición, queja o reclamo (PQR) para ser atendido y dejar constancia.

- **PBI-08** · Must · Sprint 3
- **Escenario:** El residente del 204 radica una PQR por una gotera y recibe un número y una fecha. La PQR queda Radicada.
- **Reglas:** RN-08
- **Requisitos:** REQ-PH-025
- **Criterios:** AC-025.1
- **Fuera de alcance:** adjuntar fotografías.

### HU-18 · Revisar y gestionar las PQR

Como administrador del conjunto quiero revisar y gestionar las PQR para resolverlas en orden.

- **PBI-08** · Must · Sprint 3
- **Escenario:** María, residente del 204, radica una PQR por una gotera. El administrador la pasa a En revisión y luego a En gestión. Si intenta pasarla directo a Resuelta, el sistema lo impide y la PQR conserva su estado.
- **Reglas:** RN-08
- **Requisitos:** REQ-PH-026, REQ-PH-027, REQ-PH-030
- **Criterios:** AC-026.1, AC-027.1, AC-030.1
- **Fuera de alcance:** respuestas por correo electrónico.

### HU-19 · Consultar el estado de mis PQR

Como residente quiero consultar el estado de mis PQR para saber cómo va mi solicitud.

- **PBI-08** · Must · Sprint 3
- **Escenario:** María consulta sus PQR y ve la suya con su estado y su historial. No ve las de otros residentes.
- **Reglas:** RN-08, RN-11
- **Requisitos:** REQ-PH-028
- **Criterios:** AC-028.1
- **Fuera de alcance:** notificaciones por correo.

### HU-20 · Cerrar una PQR resuelta con su respuesta

Como administrador del conjunto quiero cerrar una PQR resuelta con su respuesta para dejar constancia de la solución.

- **PBI-08** · Must · Sprint 3
- **Escenario:** El administrador cierra con una respuesta una PQR Resuelta. Después ya no se puede modificar.
- **Reglas:** RN-08
- **Requisitos:** REQ-PH-029
- **Criterios:** AC-029.1
- **Fuera de alcance:** encuestas de satisfacción.

### HU-21 · Publicar comunicados

Como administrador del conjunto quiero publicar comunicados para informar a la comunidad.

- **PBI-09** · Should · Sprint 3
- **Escenario:** El administrador publica «Corte de agua el sábado», vigente hasta el domingo. Los residentes lo ven al iniciar sesión.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-031
- **Criterios:** AC-031.1
- **Fuera de alcance:** envío por correo o mensajería.

### HU-22 · Consultar los comunicados vigentes

Como residente quiero consultar los comunicados vigentes para estar informado de lo que pasa en el conjunto.

- **PBI-09** · Should · Sprint 3
- **Escenario:** El residente ve los comunicados vigentes del más reciente al más antiguo. Un comunicado vencido ya no aparece.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-031, REQ-PH-032
- **Criterios:** AC-031.1, AC-032.1
- **Fuera de alcance:** confirmación de lectura.

### HU-23 · Autorizar la visita de una persona para una fecha

Como residente quiero autorizar la visita de una persona para una fecha para lograr que pueda ingresar sin demoras.

- **PBI-10** · Must · Sprint 3
- **Escenario:** El residente del 301 autoriza la visita de Carlos Pérez (documento ficticio) para hoy. El portero la ve como autorizada.
- **Reglas:** RN-10
- **Requisitos:** REQ-PH-033
- **Criterios:** AC-033.1
- **Fuera de alcance:** visitas recurrentes.

### HU-24 · Registrar el ingreso y la salida de los visitantes autorizados

Como portero quiero registrar el ingreso y la salida de los visitantes autorizados para controlar quién entra al conjunto.

- **PBI-10** · Must · Sprint 3
- **Escenario:** El portero registra el ingreso de Carlos a las 15:00 y su salida a las 17:00. Si llega alguien que ningún residente autorizó, el sistema no permite registrar su ingreso.
- **Reglas:** RN-10
- **Requisitos:** REQ-PH-034, REQ-PH-035
- **Criterios:** AC-034.1, AC-035.1
- **Fuera de alcance:** control biométrico.

### HU-25 · Registrar los paquetes que llegan para un apartamento

Como portero quiero registrar los paquetes que llegan para un apartamento para avisar al residente que debe recogerlos.

- **PBI-10** · Must · Sprint 3
- **Escenario:** El portero registra un paquete para el 301 y el residente lo ve pendiente de recoger.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-036
- **Criterios:** AC-036.1
- **Fuera de alcance:** fotografías del paquete.

### HU-26 · Registrar las novedades de seguridad

Como vigilante quiero registrar las novedades de seguridad para dejar constancia de lo ocurrido en el conjunto.

- **PBI-10** · Must · Sprint 3
- **Escenario:** El vigilante registra «puerta del sótano sin seguro» y el administrador ve la fecha, la hora y el autor.
- **Reglas:** RN-12
- **Requisitos:** REQ-PH-037
- **Criterios:** AC-037.1
- **Fuera de alcance:** rondas de vigilancia (objetivo extendido).

### HU-27 · Crear una orden de mantenimiento para una zona o un equipo

Como administrador del conjunto quiero crear una orden de mantenimiento para una zona o un equipo para atender los daños del conjunto.

- **PBI-11** · Must · Sprint 3
- **Escenario:** El administrador crea una orden por la bomba de agua de la torre 2 y queda en estado Solicitada.
- **Reglas:** RN-09
- **Requisitos:** REQ-PH-030, REQ-PH-038, REQ-PH-039
- **Criterios:** AC-030.1, AC-038.1, AC-039.1
- **Fuera de alcance:** compra de materiales.

### HU-28 · Aprobar las órdenes de mantenimiento que superan el tope de costo

Como consejo de administración quiero aprobar las órdenes de mantenimiento que superan el tope de costo para controlar los gastos grandes.

- **PBI-11** · Must · Sprint 3
- **Escenario:** La reparación del ascensor de la torre 2 cuesta $5.000.000 y el tope es de $2.000.000: solo el Consejo puede aprobarla. Una reparación de $800.000 la aprueba el administrador.
- **Reglas:** RN-09
- **Requisitos:** REQ-PH-040
- **Criterios:** AC-040.1
- **Fuera de alcance:** las compras y los pagos al proveedor.

### HU-29 · Ejecutar la orden y registrar los materiales usados

Como encargado de mantenimiento quiero ejecutar la orden y registrar los materiales usados para dejar constancia del trabajo y de su costo.

- **PBI-11** · Must · Sprint 3
- **Escenario:** El encargado ejecuta una orden Aprobada y registra materiales por $80.000. La orden queda Completada con ese costo.
- **Reglas:** RN-09
- **Requisitos:** REQ-PH-030, REQ-PH-041
- **Criterios:** AC-030.1, AC-041.1
- **Fuera de alcance:** pago al proveedor.

### HU-30 · Consultar las órdenes de mantenimiento que me asignaron

Como proveedor quiero consultar las órdenes de mantenimiento que me asignaron para atenderlas a tiempo.

- **PBI-11** · Must · Sprint 3
- **Escenario:** Un proveedor con 2 órdenes asignadas de 5 existentes ve solo esas 2.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-042
- **Criterios:** AC-042.1
- **Fuera de alcance:** cotizaciones y facturas (objetivo extendido).

### HU-31 · Ver el resumen de cartera y de gastos de mantenimiento

Como consejo de administración quiero ver el resumen de cartera y de gastos de mantenimiento para decidir con información.

- **PBI-12** · Should · Sprint 4
- **Escenario:** El Consejo consulta el resumen del mes y ve el total de cartera y de gastos de mantenimiento por concepto.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-043
- **Criterios:** AC-043.1
- **Fuera de alcance:** presupuesto anual (objetivo extendido).

### HU-32 · Dejar observaciones de auditoría sobre pagos y órdenes

Como revisor fiscal quiero dejar observaciones de auditoría sobre pagos y órdenes para dar seguimiento a lo que revisa.

- **PBI-12** · Should · Sprint 4
- **Escenario:** El revisor fiscal deja una observación sobre un pago y el administrador la ve con fecha y autor.
- **Reglas:** RN-11
- **Requisitos:** REQ-PH-044
- **Criterios:** AC-044.1
- **Fuera de alcance:** flujo de respuesta a las observaciones.

### HU-33 · Desplegar todos los servicios en la nube

Como equipo de desarrollo quiero desplegar todos los servicios en la nube para tener el sistema disponible y verificable por Internet.

- **PBI-13** · Must · Sprint 4
- **Escenario:** Cada uno de los 8 módulos se despliega en Render. Si falta la variable de conexión a la base de datos, el servicio no arranca y lo informa.
- **Reglas:** RN-12
- **Requisitos:** NFR-PH-005 y NFR-PH-006
- **Criterios:** —
- **Fuera de alcance:** alta disponibilidad y balanceo de carga.

### HU-34 · Contar con la matriz de trazabilidad de requisitos, criterios y pruebas

Como equipo de desarrollo quiero contar con la matriz de trazabilidad de requisitos, criterios y pruebas para demostrar con evidencia que cada requisito está probado antes del release.

- **PBI-14** · Must · Sprint 4
- **Escenario:** El equipo revisa la matriz y comprueba que cada requisito comprometido tiene criterio, tarea y prueba. Un requisito sin prueba bloquea el release.
- **Reglas:** —
- **Requisitos:** NFR-PH-007
- **Criterios:** —
- **Fuera de alcance:** herramientas externas de gestión de pruebas.

## 5. Reglas de negocio

| ID | Origen | Regla |
|---|---|---|
| RN-01 | Ley 675 de 2001 y reglamento | La cuota ordinaria mensual de cada apartamento es el presupuesto mensual multiplicado por su coeficiente de copropiedad. |
| RN-02 | Ley 675 de 2001 | La suma de los coeficientes de todos los apartamentos del conjunto es 100 %. |
| RN-03 | Ley 675 de 2001 y administración | La cuota no pagada después de su fecha límite genera un interés de mora mensual parametrizable, sin superar el máximo legal. |
| RN-04 | Contador | Un pago se registra contra una cuota y no puede superar su saldo pendiente. |
| RN-05 | Reglamento de zonas comunes | Una zona común no puede tener dos reservas en el mismo horario. |
| RN-06 | Administración | Cada apartamento tiene un máximo de reservas por mes (parámetro; valor inicial 2) y cada zona un horario de uso. |
| RN-07 | Consejo de administración | Un apartamento en mora no puede reservar zonas comunes. |
| RN-08 | Administración | La PQR avanza solo al estado siguiente (Radicada, En revisión, En gestión, Resuelta, Cerrada) y cada cambio queda auditado. |
| RN-09 | Consejo de administración | La orden de mantenimiento avanza solo al estado siguiente (Solicitada, Aprobada, En ejecución, Completada); si su costo estimado supera el tope (parámetro), la aprueba el Consejo. |
| RN-10 | Reglamento de portería | El portero solo registra el ingreso de un visitante que un residente haya autorizado. |
| RN-11 | Constitución del proyecto | Cada usuario ve solo lo que corresponde a su rol y a su apartamento (propietario y residente ven solo lo suyo). |
| RN-12 | Ley 1581 de 2012 | Los datos personales se tratan según la ley de protección de datos; los datos de prueba son ficticios. |

## 6. Parámetros del negocio

Los valores de las reglas son parámetros configurables del sistema, no constantes del código.

| Parámetro | Valor | Regla |
|---|---|---|
| Vencimiento de la cuota | El día 10 de cada mes | RN-03 |
| Interés de mora | 2 % mensual sobre el saldo vencido | RN-03 |
| Pago de una cuota | Un pago no puede superar el saldo pendiente | RN-04 |
| Reservas por apartamento | Máximo 2 por mes | RN-06 |
| Duración de una reserva | Máximo 4 horas | RN-06 |
| Quién reserva | El propietario o el residente registrado del apartamento | RN-07 |
| Cancelación de una reserva | El residente, hasta 24 horas antes; el administrador, en cualquier momento | RN-05 |
| Tope de costo de mantenimiento | $2.000.000 estimados; por encima, aprueba el Consejo | RN-09 |
| Autorización de una visita | Para una fecha; el visitante se identifica con nombre y documento | RN-10 |
| Propietarios por apartamento | Un propietario responsable del pago de las cuotas | RN-01 |
| Alcance de las cuotas | Solo cuotas ordinarias, sin cuotas extraordinarias ni multas | No-objetivos |
| Información del revisor fiscal | Cuotas, pagos y órdenes de mantenimiento con sus costos, en solo lectura | RN-11 |
| Plazo de respuesta de una PQR | Sin plazo máximo; cada PQR registra su fecha y su estado | RN-08 |

## 7. Requisitos funcionales (EARS)

Cada requisito es una sola frase con el sistema como sujeto. Patrones: ubicuo, evento (Cuando…), estado (Mientras…) y no deseado (Si…, entonces…).

| ID | Patrón | Requisito | Historia | Regla |
|---|---|---|---|---|
| REQ-PH-001 | Ubicuo | El sistema deberá permitir el acceso solo a usuarios autenticados y mostrar únicamente las opciones de su rol. | HU-02 | RN-11 |
| REQ-PH-002 | Evento | Cuando el administrador del sistema cree un usuario, el sistema deberá asignarle uno de los 11 roles y guardar su contraseña cifrada. | HU-01 | RN-11 |
| REQ-PH-003 | No deseado | Si un usuario intenta abrir una pantalla o un recurso de otro rol, entonces el sistema deberá rechazar el acceso. | HU-02 | RN-11 |
| REQ-PH-004 | Evento | Cuando el administrador registre un apartamento, el sistema deberá guardar su torre, su número y su coeficiente de copropiedad. | HU-03 | RN-02 |
| REQ-PH-005 | No deseado | Si el coeficiente de un apartamento hace que la suma de los coeficientes supere el 100 %, entonces el sistema deberá rechazar el registro e indicar la suma resultante. | HU-03 | RN-02 |
| REQ-PH-006 | Evento | Cuando el administrador registre un propietario y lo asocie a un apartamento, el sistema deberá mostrar a ese propietario solo los datos de ese apartamento. | HU-04 | RN-11 |
| REQ-PH-007 | Evento | Cuando el administrador registre a un residente en un apartamento, el sistema deberá guardarlo como residente de ese apartamento. | HU-05 | RN-11 |
| REQ-PH-008 | Evento | Cuando el administrador defina el presupuesto mensual, el sistema deberá guardarlo para ese mes. | HU-06 | RN-01 |
| REQ-PH-009 | Evento | Cuando el administrador genere las cuotas de un mes, el sistema deberá calcular la cuota de cada apartamento como el presupuesto mensual multiplicado por su coeficiente. | HU-07 | RN-01, RN-02 |
| REQ-PH-010 | No deseado | Si el mes no tiene presupuesto, entonces el sistema no deberá generar cuotas e informará el motivo. | HU-07 | RN-01 |
| REQ-PH-011 | Evento | Cuando el contador registre un pago, el sistema deberá descontarlo del saldo de la cuota. | HU-08 | RN-04 |
| REQ-PH-012 | No deseado | Si el pago supera el saldo pendiente de la cuota, entonces el sistema deberá rechazarlo e indicar el saldo. | HU-08 | RN-04 |
| REQ-PH-013 | Evento | Cuando el contador aplique la mora, el sistema deberá sumar a cada cuota vencida el interés mensual de 2 % sobre su saldo. | HU-09 | RN-03 |
| REQ-PH-014 | Estado | Mientras un apartamento tenga una cuota vencida, el sistema deberá marcarlo como en mora. | HU-11 | RN-03, RN-07 |
| REQ-PH-015 | Evento | Cuando el propietario consulte su estado de cuenta, el sistema deberá mostrar sus cuotas, sus pagos, su saldo y su mora. | HU-10 | RN-11 |
| REQ-PH-016 | Evento | Cuando el administrador filtre la cartera por torre, el sistema deberá mostrar solo los apartamentos de esa torre con su saldo. | HU-11 | RN-03 |
| REQ-PH-017 | Estado | Mientras el usuario tenga el rol de revisor fiscal, el sistema deberá permitirle solo consultar cuotas, pagos y órdenes de mantenimiento. | HU-12 | RN-11 |
| REQ-PH-018 | Evento | Cuando el administrador defina una zona común, el sistema deberá guardar su horario de uso. | HU-13 | RN-06 |
| REQ-PH-019 | Evento | Cuando un residente consulte una zona y una fecha, el sistema deberá mostrar los horarios libres y los ocupados. | HU-14 | RN-05 |
| REQ-PH-020 | Evento | Cuando un residente reserve una zona común en un horario libre, el sistema deberá confirmar la reserva. | HU-15 | RN-05 |
| REQ-PH-021 | No deseado | Si el horario solicitado ya está reservado en esa zona, entonces el sistema deberá rechazar la reserva e indicar el motivo. | HU-15 | RN-05 |
| REQ-PH-022 | No deseado | Si el apartamento está en mora, ya tiene 2 reservas en el mes o la reserva dura más de 4 horas, entonces el sistema deberá rechazarla. | HU-15 | RN-06, RN-07 |
| REQ-PH-023 | Evento | Cuando un residente cancele una reserva con al menos 24 horas de anticipación, el sistema deberá liberar el horario. | HU-16 | RN-05 |
| REQ-PH-024 | No deseado | Si faltan menos de 24 horas para la reserva, entonces el sistema deberá rechazar la cancelación del residente; el administrador puede cancelarla en cualquier momento. | HU-16 | RN-05 |
| REQ-PH-025 | Evento | Cuando un residente radique una PQR, el sistema deberá crearla en estado Radicada con número, fecha y autor. | HU-17 | RN-08 |
| REQ-PH-026 | Evento | Cuando el administrador pase una PQR al estado siguiente, el sistema deberá actualizar su estado y conservar el historial. | HU-18 | RN-08 |
| REQ-PH-027 | No deseado | Si se intenta pasar una PQR a un estado que no es el siguiente del flujo, entonces el sistema deberá rechazar el cambio. | HU-18 | RN-08 |
| REQ-PH-028 | Estado | Mientras un residente tenga la sesión iniciada, el sistema deberá mostrarle solo sus PQR con su estado y su historial. | HU-19 | RN-08, RN-11 |
| REQ-PH-029 | Evento | Cuando el administrador cierre una PQR Resuelta con una respuesta, el sistema deberá dejarla Cerrada e impedir su modificación. | HU-20 | RN-08 |
| REQ-PH-030 | Ubicuo | El sistema deberá registrar en la auditoría cada cambio de estado de una PQR o de una orden de mantenimiento, con usuario y fecha-hora. | HU-18, HU-27, HU-29 | RN-08, RN-09 |
| REQ-PH-031 | Evento | Cuando el administrador publique un comunicado, el sistema deberá mostrarlo a propietarios y residentes mientras esté vigente. | HU-21, HU-22 | RN-11 |
| REQ-PH-032 | No deseado | Si un comunicado está vencido, entonces el sistema no deberá mostrarlo. | HU-22 | RN-11 |
| REQ-PH-033 | Evento | Cuando un residente autorice una visita, el sistema deberá guardar la fecha, el nombre y el documento del visitante y mostrarla al portero como autorizada. | HU-23 | RN-10 |
| REQ-PH-034 | Evento | Cuando el portero registre el ingreso o la salida de un visitante autorizado, el sistema deberá guardarlo con fecha y hora. | HU-24 | RN-10 |
| REQ-PH-035 | No deseado | Si el portero intenta registrar el ingreso de un visitante que no fue autorizado, entonces el sistema deberá rechazarlo. | HU-24 | RN-10 |
| REQ-PH-036 | Evento | Cuando el portero registre un paquete para un apartamento, el sistema deberá mostrarlo al residente como pendiente de recoger. | HU-25 | RN-11 |
| REQ-PH-037 | Evento | Cuando el vigilante registre una novedad de seguridad, el sistema deberá guardarla con fecha, hora y autor. | HU-26 | RN-12 |
| REQ-PH-038 | Evento | Cuando el administrador cree una orden de mantenimiento, el sistema deberá crearla en estado Solicitada. | HU-27 | RN-09 |
| REQ-PH-039 | Evento | Cuando el administrador apruebe una orden cuyo costo estimado no supera el tope de $2.000.000, el sistema deberá pasarla a Aprobada. | HU-27 | RN-09 |
| REQ-PH-040 | No deseado | Si el costo estimado de una orden supera el tope de $2.000.000, entonces el sistema deberá permitir su aprobación solo al Consejo de administración. | HU-28 | RN-09 |
| REQ-PH-041 | Evento | Cuando se ejecute una orden Aprobada y se registren sus materiales, el sistema deberá pasarla a Completada con su costo. | HU-29 | RN-09 |
| REQ-PH-042 | Estado | Mientras el usuario tenga el rol de proveedor, el sistema deberá mostrarle solo las órdenes que le asignaron. | HU-30 | RN-11 |
| REQ-PH-043 | Evento | Cuando el Consejo consulte el resumen, el sistema deberá mostrar el total de cartera y de gastos de mantenimiento por concepto. | HU-31 | RN-11 |
| REQ-PH-044 | Evento | Cuando el revisor fiscal deje una observación de auditoría, el sistema deberá guardarla con fecha y autor. | HU-32 | RN-11 |
| REQ-PH-045 | Ubicuo | El sistema deberá tratar los datos personales de residentes y visitantes según la Ley 1581 de 2012 y usar solo datos ficticios en las pruebas. | HU-05 | RN-12 |

## 8. Requisitos no funcionales

| ID | Categoría | Historias | Requisito medible |
|---|---|---|---|
| NFR-PH-001 | Rendimiento | HU-10 | El estado de cuenta de un propietario responde en menos de 1 segundo con 120 apartamentos. |
| NFR-PH-002 | Seguridad | HU-01, HU-02 | Las contraseñas se guardan cifradas y ninguna pantalla ni recurso de un rol es accesible por otro rol, verificado con una prueba por cada uno de los 11 roles. |
| NFR-PH-003 | Auditoría | HU-18, HU-27, HU-29 | El 100 % de los cambios de estado de cuotas, reservas, PQR y órdenes queda registrado con usuario y fecha-hora. |
| NFR-PH-004 | Privacidad | HU-05 | Ningún dato personal real aparece en el repositorio, en las capturas ni en las pruebas. |
| NFR-PH-005 | Despliegue | HU-33 | Cada servicio arranca desde su contenedor y, si falta la variable de conexión a la base de datos, no arranca y lo informa con un mensaje claro. |
| NFR-PH-006 | Contrato | HU-33 | Cada servicio publica su contrato OpenAPI (Swagger) y este coincide con su comportamiento. |
| NFR-PH-007 | Trazabilidad | HU-34 | El 100 % de los requisitos comprometidos tiene criterio de aceptación, tarea y prueba en la matriz de trazabilidad. |

## 9. Criterios de aceptación (Dado / Cuando / Entonces)

| AC | Requisito | Dado | Cuando | Entonces |
|---|---|---|---|---|
| AC-001.1 | REQ-PH-001 | Un portero con la sesión iniciada | abre su página de inicio | ve solo las opciones de portería. |
| AC-002.1 | REQ-PH-002 | Un administrador del sistema | crea un usuario con el rol Contador | el usuario queda con ese rol y su contraseña no se guarda en texto plano. |
| AC-003.1 | REQ-PH-003 | Un residente con la sesión iniciada | intenta abrir una pantalla del contador | el sistema rechaza el acceso. |
| AC-004.1 | REQ-PH-004 | La torre 1 | el administrador registra el apartamento 301 con coeficiente de 1,5 % | el apartamento queda guardado en la torre 1 con ese coeficiente. |
| AC-005.1 | REQ-PH-005 | Un conjunto cuyos coeficientes suman 99 % | el administrador registra un apartamento con coeficiente de 2 % | el sistema lo rechaza e indica que la suma sería 101 %. |
| AC-006.1 | REQ-PH-006 | Un propietario asociado al apartamento 301 | inicia sesión | ve los datos del 301 y no los del 302. |
| AC-007.1 | REQ-PH-007 | Un apartamento con dos residentes registrados | se consulta el apartamento | se ven los dos residentes. |
| AC-008.1 | REQ-PH-008 | Un presupuesto de $10.000.000 para noviembre | el administrador lo guarda | queda registrado para noviembre. |
| AC-009.1 | REQ-PH-009 | Un presupuesto mensual de $10.000.000 y un apartamento con coeficiente de 1,5 % | se generan las cuotas del mes | la cuota del apartamento es de $150.000. |
| AC-009.2 | REQ-PH-009 | Un conjunto cuyos coeficientes suman 100 % | se generan las cuotas del mes | la suma de todas las cuotas es igual al presupuesto mensual. |
| AC-010.1 | REQ-PH-010 | Un mes sin presupuesto | el administrador intenta generar las cuotas | el sistema no crea ninguna cuota e informa el motivo. |
| AC-011.1 | REQ-PH-011 | Una cuota con saldo de $150.000 | el contador registra un pago de $100.000 | el saldo de la cuota queda en $50.000. |
| AC-012.1 | REQ-PH-012 | Una cuota con saldo de $150.000 | el contador registra un pago de $200.000 | el sistema rechaza el pago e indica el saldo de $150.000. |
| AC-013.1 | REQ-PH-013 | Una cuota de $150.000 vencida (pasó el día 10) | el contador aplica la mora | se suman $3.000 de interés a la cuota. |
| AC-014.1 | REQ-PH-014 | El apartamento 301 con una cuota vencida | el administrador consulta la cartera | el 301 aparece en mora. |
| AC-015.1 | REQ-PH-015 | Un propietario con una cuota vencida de $150.000 y una mora de $3.000 | consulta su estado de cuenta | ve un saldo de $153.000 y una mora de $3.000. |
| AC-016.1 | REQ-PH-016 | Apartamentos con saldo en las torres 1 y 2 | se filtra la cartera por la torre 1 | se ven solo los de la torre 1 con su saldo. |
| AC-017.1 | REQ-PH-017 | Un revisor fiscal con la sesión iniciada | intenta modificar un pago | el sistema lo impide. |
| AC-018.1 | REQ-PH-018 | La zona salón social con horario de 8:00 a 22:00 | el administrador la guarda | la zona admite reservas solo dentro de ese horario. |
| AC-019.1 | REQ-PH-019 | Una reserva de 14:00 a 16:00 en el salón social | otro residente consulta ese día | el horario de 14:00 a 16:00 aparece ocupado. |
| AC-020.1 | REQ-PH-020 | Un horario libre y un apartamento al día | el residente reserva de 14:00 a 16:00 | la reserva queda confirmada. |
| AC-021.1 | REQ-PH-021 | Una reserva de 14:00 a 16:00 en el salón social | otro residente reserva de 15:00 a 17:00 | el sistema rechaza la reserva por cruce de horario. |
| AC-022.1 | REQ-PH-022 | Un apartamento en mora | su residente intenta reservar una zona común | el sistema rechaza la reserva e indica que hay mora. |
| AC-022.2 | REQ-PH-022 | Un apartamento con 2 reservas en el mes | su residente intenta una tercera | el sistema rechaza la reserva e indica el máximo mensual. |
| AC-023.1 | REQ-PH-023 | Una reserva para el sábado a las 14:00 | el residente la cancela el jueves | el horario queda libre. |
| AC-024.1 | REQ-PH-024 | Una reserva para el sábado a las 14:00 | el residente intenta cancelarla el sábado a las 10:00 | el sistema rechaza la cancelación. |
| AC-025.1 | REQ-PH-025 | Un residente del apartamento 204 | radica una PQR por una gotera | la PQR queda en estado Radicada con número, fecha y autor. |
| AC-026.1 | REQ-PH-026 | Una PQR Radicada | el administrador la pasa a En revisión | su estado es En revisión y el historial conserva el cambio. |
| AC-027.1 | REQ-PH-027 | Una PQR en estado Radicada | el administrador intenta pasarla a Resuelta | el sistema rechaza el cambio y la PQR sigue Radicada. |
| AC-028.1 | REQ-PH-028 | Dos PQR de residentes distintos | uno de ellos consulta sus PQR | ve solo la suya con su estado y su historial. |
| AC-029.1 | REQ-PH-029 | Una PQR Resuelta | el administrador la cierra con una respuesta | la PQR queda Cerrada y no se puede modificar. |
| AC-030.1 | REQ-PH-030 | Una PQR que pasa a En revisión | se consulta la auditoría | aparece el cambio con el usuario y la fecha-hora. |
| AC-031.1 | REQ-PH-031 | Un comunicado vigente | un residente inicia sesión | lo ve en su inicio. |
| AC-032.1 | REQ-PH-032 | Un comunicado vencido | un residente consulta los comunicados | el comunicado no aparece. |
| AC-033.1 | REQ-PH-033 | Una visita autorizada para hoy | el visitante llega | el portero la ve como autorizada. |
| AC-034.1 | REQ-PH-034 | Un visitante autorizado | el portero registra su ingreso | el ingreso queda guardado con la hora. |
| AC-035.1 | REQ-PH-035 | Un visitante que ningún residente autorizó | el portero intenta registrar su ingreso | el sistema lo rechaza. |
| AC-036.1 | REQ-PH-036 | Un paquete registrado para el 301 | el residente consulta | lo ve pendiente de recoger. |
| AC-037.1 | REQ-PH-037 | Una novedad registrada | el administrador la consulta | ve la fecha, la hora y el autor. |
| AC-038.1 | REQ-PH-038 | Una zona dañada | el administrador crea la orden | la orden queda en estado Solicitada. |
| AC-039.1 | REQ-PH-039 | Una orden Solicitada con costo estimado de $800.000 | el administrador la aprueba | la orden pasa a Aprobada. |
| AC-040.1 | REQ-PH-040 | Una orden Solicitada con costo estimado de $5.000.000 | el administrador intenta aprobarla | el sistema exige la aprobación del Consejo y no la aprueba. |
| AC-041.1 | REQ-PH-041 | Una orden Aprobada | el encargado la ejecuta y registra materiales por $80.000 | la orden pasa a Completada con un costo de $80.000. |
| AC-042.1 | REQ-PH-042 | Un proveedor con 2 órdenes asignadas de 5 existentes | inicia sesión | ve solo esas 2 órdenes. |
| AC-043.1 | REQ-PH-043 | La cartera y los gastos del mes | el Consejo consulta el resumen | ve el total por cada concepto. |
| AC-044.1 | REQ-PH-044 | Una observación del revisor fiscal | el administrador consulta | la ve con fecha y autor. |
| AC-045.1 | REQ-PH-045 | Los datos y las pruebas del proyecto | se revisan antes de cada entrega | no hay datos personales reales. |

## 10. Entidades clave

| Entidad | Qué es |
|---|---|
| Conjunto y torre | El conjunto residencial y sus 3 torres. |
| Apartamento | Unidad privada con torre, número y coeficiente de copropiedad. |
| Propietario y residente | Personas asociadas a un apartamento; el propietario responde por las cuotas. |
| Usuario y rol | Persona con acceso al sistema y uno de los 11 roles. |
| Presupuesto mensual y cuota | Valor a recaudar en el mes y la parte que corresponde a cada apartamento. |
| Pago | Abono registrado contra una cuota. |
| Zona común y reserva | Espacio compartido con horario y la reserva de un horario por un apartamento. |
| PQR y comunicado | Petición, queja o reclamo con su flujo de estados; aviso publicado a la comunidad. |
| Visita, paquete y novedad | Registros de portería y de vigilancia. |
| Orden de mantenimiento | Trabajo por hacer en una zona o equipo, con su flujo de aprobación y su costo. |
| Proveedor | Empresa o persona que atiende órdenes de mantenimiento. |

## 11. Restricciones, supuestos y riesgos

**Restricciones y supuestos**

- No hay usuarios reales: el caso es ficticio y todos los datos de prueba son ficticios.
- Tecnología fija por el curso: Java 17, Spring Boot, MongoDB Atlas, Thymeleaf con Bootstrap, Docker y Render; microservicios con una base de datos por servicio.
- Los planes gratuitos de Atlas y Render tienen límites de memoria y de tiempo activo; los servicios pueden tardar en despertar.
- El equipo es de 3 personas y trabaja en sesiones de lunes y martes más trabajo asíncrono.
- La Ley 675 de 2001 (propiedad horizontal) y la Ley 1581 de 2012 (datos personales) son la referencia legal; no se reproduce toda la ley.
- Los pagos son registros manuales del contador; no hay pasarela de pagos.

**Riesgos**

- Carga: el backlog comprometido es ajustado para un equipo de 3 (unos 25 puntos por sprint); el objetivo extendido absorbe la variación.
- Despliegue: la mayor incertidumbre técnica es desplegar 8 módulos en Render (PBI-13, 13 puntos).
- Alcance: si la clarificación abre alcance nuevo, entra como solicitud de cambio (CR-###) por el Product Backlog.

## 12. Glosario

| Término | Definición |
|---|---|
| Coeficiente de copropiedad | Porcentaje que representa un apartamento en el conjunto; define su parte de las cuotas. |
| Cuota ordinaria | Valor mensual de administración que paga cada apartamento. |
| Mora | Atraso en el pago de una cuota, que genera un interés. |
| PQR | Petición, queja o reclamo de un residente o propietario. |
| Zona común | Espacio compartido del conjunto que se puede reservar (salón social, zona de barbacoa, etc.). |
| Consejo de administración | Órgano que aprueba los gastos que superan el tope. |
| Revisor fiscal | Quien audita la contabilidad del conjunto. |
| Puerta | Sesión corta de revisión humana de un documento de SDD dentro del sprint. |
| Delta de spec | Descripción de lo que se añade, modifica o elimina de la especificación por un cambio. |

## Historial

| Versión | Fecha | Cambio |
|---|---|---|
| 0.1 | 2026-11-08 | Contexto, actores, historias con escenario, reglas (D03) |
| 0.2 | 2026-11-08 | Parámetros del negocio definidos en la clarificación (D04, `docs/actas/clarificacion.md`) |
| 1.0 | 2026-11-08 | Requisitos EARS, requisitos no funcionales y criterios de aceptación (D05) |
