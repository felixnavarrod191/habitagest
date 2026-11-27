# Despliegue en Render y Atlas

Versión 1.0 · 2026-11-26 · HU-33, NFR-PH-005, NFR-PH-006, ADR-005 y ADR-006. Responsable: Felix José Navarro Ortiz · Revisa: Angel Daniel Moreno Lazcano.

El sistema se despliega con 8 servicios de Render (uno por módulo) descritos en `render.yaml`, que usa el `Dockerfile` de cada carpeta, y un clúster de MongoDB Atlas con una base de datos por servicio.

## 1. Servicios y variables

| Servicio de Render | Carpeta | Variables que se escriben a mano | Variables ya definidas en `render.yaml` |
|---|---|---|---|
| torres-mirador-copropiedad | ms-copropiedad | `MONGODB_URI` | `JAVA_TOOL_OPTIONS` |
| torres-mirador-finanzas | ms-finanzas | `MONGODB_URI` | `COPROPIEDAD_URL`, `MANTENIMIENTO_URL` |
| torres-mirador-reservas | ms-reservas | `MONGODB_URI` | `COPROPIEDAD_URL`, `FINANZAS_URL` |
| torres-mirador-pqr | ms-pqr | `MONGODB_URI` | — |
| torres-mirador-porteria | ms-porteria | `MONGODB_URI` | `COPROPIEDAD_URL` |
| torres-mirador-mantenimiento | ms-mantenimiento | `MONGODB_URI` | — |
| torres-mirador-gateway | api-gateway | — | las seis `*_URL` de los servicios |
| torres-mirador-web | web | `MONGODB_URI`, `ADMIN_PASSWORD`, `DEMO_PASSWORD` | `GATEWAY_URL`, `APP_DEMO=true` |

Cada servicio lee el puerto de la variable `PORT` que Render asigna y publica su salud en `/actuator/health`. Ninguna clave va en el repositorio.

## 2. Preparar MongoDB Atlas

1. En Atlas, crea un usuario de base de datos con permiso de lectura y escritura (por ejemplo `torres_app`) y una contraseña propia.
2. En Network Access, permite el acceso desde cualquier dirección (`0.0.0.0/0`): Render usa direcciones de salida que cambian.
3. Copia la cadena de conexión del clúster (`mongodb+srv://…`). Se usa la misma cadena en los 7 servicios con base de datos: cada servicio crea su propia base (`copropiedad_db`, `finanzas_db`, `reservas_db`, `pqr_db`, `porteria_db`, `mantenimiento_db`, `web_db`).

## 3. Crear los servicios con el Blueprint

1. Confirma que `main` tiene `render.yaml` en la raíz del repositorio.
2. En Render: New → Blueprint, conecta el repositorio y elige la rama `main`.
3. Render lista los 8 servicios y pide los valores marcados como secretos. Escribe `MONGODB_URI` (la cadena de Atlas), `ADMIN_PASSWORD` y `DEMO_PASSWORD` (claves nuevas, que no se comparten por el repositorio).
4. Pulsa Apply. La primera construcción de cada servicio tarda varios minutos (compila con Maven dentro de Docker).
5. Si Render cambia el nombre de algún servicio porque ya existe, corrige las direcciones `*_URL` y `GATEWAY_URL` del Blueprint o del panel para que coincidan con las direcciones reales `https://<nombre>.onrender.com`.

Orden recomendado si se crean a mano: ms-copropiedad, ms-mantenimiento, ms-finanzas, ms-reservas, ms-pqr, ms-porteria, api-gateway y, al final, web.

## 4. Verificación (NFR-PH-005)

1. Ejecuta `verificar-despliegue.ps1`: comprueba `/actuator/health` de los 8 servicios y `/swagger-ui.html` de los 6 de negocio. Los 8 deben responder `UP` o 200.
2. Abre la web desplegada, entra con `admin` y la clave `ADMIN_PASSWORD`, y con cada usuario `demo_<rol>` y la clave `DEMO_PASSWORD`.
3. Prueba de arranque sin base: en un servicio de prueba, borra `MONGODB_URI` y confirma en los logs que el módulo no arranca y dice que falta la variable (también lo cubre `ConfiguracionObligatoriaTest`).
4. Con `APP_DEMO=true` los usuarios de demostración existen. Para una entrega real, cambia `APP_DEMO` a `false` y vuelve a desplegar la web.

## 5. Límites del plan gratuito

- Cada servicio se duerme tras unos 15 minutos sin uso y la primera petición tarda cerca de un minuto en despertarlo. Antes de una demostración, ejecuta `verificar-despliegue.ps1` para despertarlos todos.
- Cada servicio dispone de 512 MB: `JAVA_TOOL_OPTIONS` limita la memoria de Java al 70 % para no ser terminado por falta de memoria.
- Las horas gratuitas mensuales se comparten entre los servicios de la cuenta de Render.

## 6. Problemas frecuentes

| Síntoma | Causa probable | Qué hacer |
|---|---|---|
| El servicio queda en Deploy failed y los logs dicen que falta `MONGODB_URI` | No se escribió el secreto | Agrégalo en Environment y vuelve a desplegar |
| `MongoTimeoutException` | Atlas no permite la dirección de Render o la clave tiene caracteres especiales sin codificar | Revisa Network Access y codifica la clave en la cadena |
| La web carga pero las pantallas dan error 503 | El gateway o un servicio dormía | Espera un minuto y recarga; confirma las `*_URL` |
| El resumen del Consejo da 503 | ms-finanzas no alcanza a ms-mantenimiento | Revisa `MANTENIMIENTO_URL` de ms-finanzas |
| Fuera de memoria | El plan gratuito tiene 512 MB | Mantén `JAVA_TOOL_OPTIONS` y despierta los servicios de uno en uno |
