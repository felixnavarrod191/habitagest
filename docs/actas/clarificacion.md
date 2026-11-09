# Acta de clarificación (paso 2 de SDD)

- **Fecha de la revisión:** 8 de noviembre de 2026
- **Modalidad:** revisión por escrito en el pull request (sin reunión)
- **Participantes:** Felix José Navarro Ortiz, Angel Daniel Moreno Lazcano y Estiven Alexander Rodríguez Pérez (cada uno responde por escrito en el pull request)
- **Facilita y redacta:** Angel Daniel Moreno Lazcano · **Revisa:** Felix José Navarro Ortiz
- **Criterios de decisión, en este orden:** lo que dice la ley (675 de 2001 y 1581 de 2012), lo más simple que cumpla el objetivo y lo que se pueda verificar.

## Decisiones

### D-01 · Vencimiento de la cuota

- **Opciones:** Día 5, día 10 o día 15 de cada mes
- **Decisión:** Día 10 de cada mes
- **Regla:** RN-03 · **Requisitos afectados:** REQ-PH-013

### D-02 · Interés de mora

- **Opciones:** 1,5 %, 2 % o la tasa máxima legal
- **Decisión:** 2 % mensual sobre el saldo vencido, parametrizable y sin superar el máximo legal
- **Regla:** RN-03 · **Requisitos afectados:** REQ-PH-013

### D-03 · Reservas por apartamento

- **Opciones:** 1, 2 o 4 por mes
- **Decisión:** Máximo 2 por mes
- **Regla:** RN-06 · **Requisitos afectados:** REQ-PH-022

### D-04 · Duración de una reserva

- **Opciones:** 2, 4 u 8 horas
- **Decisión:** Máximo 4 horas
- **Regla:** RN-06 · **Requisitos afectados:** REQ-PH-022

### D-05 · Quién reserva

- **Opciones:** Solo el propietario, solo el residente o ambos
- **Decisión:** El propietario o el residente registrado del apartamento
- **Regla:** RN-07 · **Requisitos afectados:** REQ-PH-020

### D-06 · Cancelación de una reserva

- **Opciones:** Sin plazo, 24 horas o 48 horas
- **Decisión:** El residente, hasta 24 horas antes; el administrador, en cualquier momento
- **Regla:** RN-05 · **Requisitos afectados:** REQ-PH-023, REQ-PH-024

### D-07 · Tope de costo de mantenimiento

- **Opciones:** $1.000.000, $2.000.000 o $5.000.000
- **Decisión:** $2.000.000 estimados; por encima, aprueba el Consejo
- **Regla:** RN-09 · **Requisitos afectados:** REQ-PH-039, REQ-PH-040

### D-08 · Autorización de una visita

- **Opciones:** Una fecha o recurrente; con o sin documento
- **Decisión:** Para una fecha; el visitante se identifica con nombre y documento
- **Regla:** RN-10 · **Requisitos afectados:** REQ-PH-033

### D-09 · Propietarios por apartamento

- **Opciones:** Uno o varios con reparto
- **Decisión:** Un propietario responsable del pago de las cuotas
- **Regla:** RN-01 · **Requisitos afectados:** REQ-PH-006

### D-10 · Cuotas extraordinarias y multas

- **Opciones:** Incluirlas o dejarlas fuera
- **Decisión:** Fuera de alcance de esta versión (no-objetivo)
- **Regla:** No-objetivos · **Requisitos afectados:** —

### D-11 · Información del revisor fiscal

- **Opciones:** Solo cuotas y pagos, o también órdenes de mantenimiento
- **Decisión:** Cuotas, pagos y órdenes de mantenimiento con sus costos, en solo lectura
- **Regla:** RN-11 · **Requisitos afectados:** REQ-PH-017

### D-12 · Plazo de respuesta de una PQR

- **Opciones:** Sin plazo máximo, plazo único o plazo según el tipo de PQR
- **Decisión:** Sin plazo máximo en esta versión; cada PQR registra su fecha y su estado
- **Regla:** RN-08 · **Requisitos afectados:** —

## Resultado

La spec pasa de la versión 0.1 a la 0.2 con estas decisiones incorporadas. No queda ningún marcador [NECESITA ACLARACIÓN] en `specs/001-habitagest/`.

## Respuestas por escrito

- **Felix José Navarro Ortiz (revisor de D04):** de acuerdo con las 12 decisiones (D-01 a D-12).
- **Estiven Alexander Rodríguez Pérez (revisor de D03):** de acuerdo con las 12 decisiones (D-01 a D-12).
- **Decisión final:** las 12 decisiones quedan como están en esta acta y en la tabla de parámetros de la spec.
