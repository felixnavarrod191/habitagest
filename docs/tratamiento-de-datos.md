# Tratamiento de datos personales

Versión 1.0 · 2026-11-26 · Cubre REQ-PH-045, NFR-PH-004 y RN-12 (Ley 1581 de 2012).

## 1. Qué datos personales maneja el sistema

| Dato | Dónde se guarda | Quién lo ve |
|---|---|---|
| Nombre y documento de propietarios y residentes | ms-copropiedad (copropiedad_db) | Administrador del conjunto; cada propietario o residente solo ve lo de su apartamento |
| Nombre y documento de los visitantes | ms-porteria (porteria_db) | Residente que autoriza, portero y administrador del conjunto |
| Nombre completo y documento de todos los usuarios, de cualquier rol | web (web_db) | Administrador del sistema |
| Usuario, rol y contraseña cifrada con BCrypt | web (web_db) | Administrador del sistema; la contraseña no se muestra a nadie |
| Autor de PQR, novedades, pagos, órdenes y observaciones | Cada servicio, con el nombre de usuario | Según el rol de cada pantalla (RN-11) |

## 2. Finalidad y acceso

Los datos se usan solo para administrar el conjunto: cuotas, reservas, PQR, portería, mantenimiento y auditoría. Cada rol ve lo que le corresponde (RN-11) y el acceso a otras rutas se rechaza con la página 403 (REQ-PH-003). Las contraseñas se guardan cifradas y nunca van en el repositorio: las claves salen de variables de entorno (`ADMIN_PASSWORD`, `DEMO_PASSWORD`, `MONGODB_URI`).

## 3. Datos de prueba

Todos los datos del repositorio, de las pruebas automáticas, de los scripts de demostración y de las capturas son ficticios: conjunto, torres, apartamentos, nombres, documentos y usuarios `demo_*`. No se usan datos de personas reales ni se guardan en el repositorio.

## 4. Contacto del equipo

La página de aterrizaje publica el correo de cada integrante del equipo de desarrollo para que los usuarios pidan ayuda. Es una decisión de los tres integrantes, que publican sus propios correos. Esos tres correos solo aparecen en la página de aterrizaje y en su prueba; `DatosPersonalesTest` rechaza cualquier otro correo real y comprueba que el contacto no aparezca en otros archivos.

## 5. Cómo se revisa antes de cada entrega

1. `DatosPersonalesTest` recorre los archivos de texto del repositorio y falla si encuentra un correo o un teléfono móvil que no sea ficticio (AC-045.1).
2. En cada pull request, el revisor busca datos reales en el diff y en las capturas.
3. Antes del release, el equipo ejecuta `mvn verify` en `web` y confirma que la prueba pasa.
