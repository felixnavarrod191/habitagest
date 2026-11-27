# ADR-006 · El resumen de cartera y gastos vive en ms-finanzas y consulta a ms-mantenimiento por REST

- **Estado:** aceptado (Planning del Sprint 4)
- **Fecha:** 2026-11-26
- **Requisitos relacionados:** REQ-PH-043, RN-11

## Contexto

El Consejo necesita ver en una sola consulta el total de cartera (datos de ms-finanzas) y el total de gastos de mantenimiento (datos de ms-mantenimiento). La regla de dependencia del plan dice que un servicio nunca lee la base de otro y que, si necesita un dato ajeno, lo pide por REST al servicio dueño. El contrato de finanzas ya declaraba `GET /resumen` con ambos totales y el de mantenimiento ya publicaba `GET /gastos`.

## Decisión

`GET /resumen?mes=AAAA-MM` se implementa en ms-finanzas. El servicio suma el saldo de las cuotas del mes y pide a ms-mantenimiento, por REST (`GET /gastos?mes=AAAA-MM`), el total de gastos del mes. La dirección de ms-mantenimiento se configura con la variable `MANTENIMIENTO_URL` (por defecto `http://localhost:8086`). Si ms-mantenimiento no responde, el resumen devuelve 503 con el mensaje de error del contrato.

## Alternativas consideradas

- Que la web llame a los dos servicios y arme el resumen: deja la regla fuera de un servicio, no la cubre una prueba de servicio y el contrato de finanzas quedaría sin implementar.
- Un servicio nuevo de reportes: agrega un noveno módulo y su despliegue sin un requisito que lo pida.

## Consecuencias

Aparece una dependencia nueva, ms-finanzas → ms-mantenimiento (se agrega a la tabla del plan) y el servicio de finanzas necesita `MANTENIMIENTO_URL` al desplegarse en Render.
