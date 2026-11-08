# HabitaGest

Sistema de gestión de un conjunto residencial (cuotas y mora, reservas, PQR, portería y mantenimiento) con 11 roles. Proyecto del curso, con Scrum aplicado a SDD.

## Módulos

| Módulo | Puerto | Base de datos |
|---|---|---|
| ms-copropiedad | 8081 | copropiedad_db |
| ms-finanzas | 8082 | finanzas_db |
| ms-reservas | 8083 | reservas_db |
| ms-pqr | 8084 | pqr_db |
| ms-porteria | 8085 | porteria_db |
| ms-mantenimiento | 8086 | mantenimiento_db |
| web | 8080 | web_db |
| api-gateway | 8090 | — |

## Requisitos

- Java 17 y Maven (o Spring Tools for Eclipse con soporte de Maven).
- Un clúster de MongoDB Atlas con acceso desde tu IP.

## Variables de entorno

Nunca se guardan en el repositorio.

| Variable | Dónde | Para qué |
|---|---|---|
| `MONGODB_URI` | Los 7 módulos con base de datos | Cadena de conexión a Atlas. Sin ella, el módulo no arranca y lo informa. |
| `ADMIN_PASSWORD` | web | Contraseña del administrador del sistema que se crea si la base está vacía. |
| `APP_DEMO` y `DEMO_PASSWORD` | web | Con `APP_DEMO=true` crea un usuario de demostración por rol, con la contraseña de `DEMO_PASSWORD`. |
| `COPROPIEDAD_URL`, `FINANZAS_URL`, `RESERVAS_URL`, `PQR_URL`, `PORTERIA_URL`, `MANTENIMIENTO_URL` | api-gateway | Direcciones de los servicios (por defecto, localhost). |

## Cómo arrancar un módulo en local

1. Define `MONGODB_URI` en la configuración de ejecución del IDE (Run Configurations → Environment) o en tu terminal.
2. Desde la carpeta del módulo: `mvn verify` y luego `mvn spring-boot:run`.
3. Comprueba `http://localhost:<puerto>/actuator/health` (debe responder `UP`) y `http://localhost:<puerto>/swagger-ui.html`.
4. Para la web: define también `ADMIN_PASSWORD`; entra en `http://localhost:8080` con el usuario `admin`.

## Docker

Cada módulo tiene su `Dockerfile` de dos etapas: `docker build -t habitagest-<modulo> .` dentro de la carpeta del módulo.

## Documentación del proyecto

- `constitution.md` y `AGENTS.md`: reglas del proyecto y de la IA.
- `specs/001-habitagest/`: spec, plan, tareas y contratos OpenAPI.
- `docs/`: acuerdos, trazabilidad, ADR, bitácora de IA y actas.
