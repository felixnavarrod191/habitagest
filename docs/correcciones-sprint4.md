# Correcciones y mejoras tras las pruebas web del Sprint 4

Versión 1.0 · 2026-11-27 · Responsable: Felix José Navarro Ortiz · Revisa: Angel Daniel Moreno Lazcano.

Los tres integrantes recorrieron la web completa con la guía de pruebas. Estos son los cambios que salieron de ese recorrido y que se entregan en un solo pull request (PR-30).

| N.º | Cambio | Dónde | Prueba |
|---|---|---|---|
| 1 | Nombre completo y documento obligatorios para los 11 roles. Torre y apartamento siguen siendo solo del propietario y del residente. Quien no los tenga puede completarlos desde Usuarios. | web: `ControladorUsuarios`, `DatosIniciales`, `usuarios.html` | `UsuariosControladorTest` |
| 2 | Lista de presupuestos guardados en Cuotas y mora, del mes más reciente al más antiguo. | ms-finanzas: `GET /presupuestos/todos`; web: `ControladorContabilidad` | `ServicioCuotasTest`, `CorreccionesDelSprint4Test`, `ContratoOpenApiTest` |
| 3 | El selector del resumen del Consejo conserva el mes consultado. | web: `ControladorConsejo`, `Pantallas` | `CorreccionesDelSprint4Test` |
| 4 | Un mes sin cartera ni gastos muestra el aviso de que no hay datos, en lugar de los conceptos en cero. | web: `ControladorConsejo` | `CorreccionesDelSprint4Test` |
| 5 | La tabla de visitas del portero muestra la hora de entrada y de salida. | ms-porteria: `GET /ingresos?fecha=`; web: `ControladorPorteriaWeb` | `ServicioPorteriaTest`, `CorreccionesDelSprint4Test`, `ContratoOpenApiTest` |
| 6 | Página de error 404 con el diseño de la web, en lugar de la página blanca. | web: `templates/error/404.html` | `CorreccionesDelSprint4Test` |
| 7 | Fondo de la página de aterrizaje: imagen de tarde en modo claro e imagen de noche en modo oscuro. | web: `aterrizaje.html`, `static/img/`, `SeguridadConfig` | `AterrizajeTest` |

## Decisiones

- La hora de entrada y de salida ya se guardaba en cada ingreso (REQ-PH-034); solo faltaba consultarla y mostrarla. No se cambió la visita.
- Los usuarios que ya existen sin nombre y documento no se borran: el administrador y los usuarios de demostración se completan al arrancar la web, y los demás se completan desde la pantalla Usuarios.
- Las imágenes de fondo se sirven sin iniciar sesión (`/img/**`), porque la página de aterrizaje es pública.
- Los datos personales de los usuarios de demostración son ficticios (`Demo <rol>` y documentos de siete cifras que empiezan por 9).

## Cómo se verifica

1. `mvn verify` en `web`, `ms-finanzas` y `ms-porteria`.
2. Repetir en la web las pruebas A5 a A14, B0 y B2.1, B3, C3.3 a C3.5 y E1 a E4 de la guía de pruebas, y la prueba F de la página de aterrizaje en modo claro y en modo oscuro.
