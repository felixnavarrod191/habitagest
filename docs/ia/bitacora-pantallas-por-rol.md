# Bitácora de IA · Módulo de pantallas por rol

## 2026-11-20 · Módulo de pantallas por rol (modificación aparte, fuera del Sprint 3)

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** modificación aparte, sin PBI, sin puntos y sin tarea T### · **Responsable:** Felix José Navarro Ortiz
- **Qué se pidió:** las pantallas web de cada rol (formularios y tablas que llaman a los servicios por el gateway) y sus pruebas, a partir de la spec v1.0, de los contratos y del documento del docente.
- **Archivos generados:** web/ (ClienteApi, pantallas, controladores de cada área, plantilla pantalla.html y pruebas) y ms-reservas/ (listado de reservas por apartamento)
- **Qué se aceptó:** el código y las pruebas generados, tras leer el diff completo.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** se ejecuta `mvn verify` de web y de ms-reservas, se corre `verificar-pantallas.ps1` y se recorre cada rol en el navegador con los usuarios de demostración.
- **Ajustes tras la verificación:** ninguno registrado.
