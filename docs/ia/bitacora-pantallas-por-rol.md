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
- **Corrección posterior:** el residente y el propietario ya no eligen apartamento; cada usuario tiene el que le asignó el administrador del sistema (campo apartamentoId en Usuario, pantalla /sistema/usuarios). Archivos: Usuario, ControladorUsuarios, ControladorResidentes, usuarios.html y sus pruebas (web: 196 con el rediseño).
- **Rediseño de la web:** menú lateral con el nombre del usuario y, para propietario y residente, su apartamento debajo; botón de modo oscuro y claro (guardado en el navegador). Archivos: fragmentos.html, pantalla.html, DatosDePagina (nuevo), application.properties (salida completa de la página, para que no se corte el inicio de sesión) y sus pruebas. Se escogió la opción B entre tres propuestas de diseño.

## 2026-11-22 · Usuarios con apartamento obligatorio (modificación aparte, fuera del Sprint 3)

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** modificación aparte, sin PBI, sin puntos y sin tarea T### · **Responsable:** Felix José Navarro Ortiz
- **Qué se pidió:** que al crear un usuario con rol Propietario o Residente se indique su nombre completo, su documento, su torre y su apartamento (en dos selectores), que los demás roles no lleven apartamento, y que Unidades deje de tener los formularios de registrar propietario y residente.
- **Archivos generados:** web/…/usuarios/ControladorUsuarios.java y Usuario.java, templates/usuarios.html, areas/ControladorAdministracion.java, pantallas/Areas.java y UsuariosControladorTest.java.
- **Qué se aceptó:** el código y las pruebas generados, tras leer el diff completo. La web registra a la persona en ms-copropiedad al crear el usuario, de modo que la regla de un solo propietario por apartamento sigue en el servicio.
- **Qué se descartó y por qué:** cambiar el apartamento de un usuario que ya lo tiene, porque ms-copropiedad no tiene operación para modificar o dar de baja a un propietario o residente y los dos registros quedarían distintos.
- **Cómo se verificó:** `mvn verify` de web y recorrido de la guía de pruebas en la web (pasos A12 a A14 y B1.5 a B1.7).

## 2026-11-25 · Descripción y apartamento en las PQR, materiales en las órdenes de mantenimiento (modificación aparte)

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** modificación aparte, sin PBI, sin puntos y sin tarea T### · **Responsable:** Felix José Navarro Ortiz
- **Qué se pidió:** que en las tablas de PQR se vea la descripción, que el administrador del conjunto vea la torre y el apartamento de quien radica la PQR, y que las tablas de órdenes de mantenimiento muestren los materiales registrados.
- **Archivos generados:** web/…/areas/ControladorAdministracion.java, ControladorConsejo.java, ControladorMantenimientoWeb.java, ControladorResidentes.java y pantallas/Pantallas.java.
- **Qué se aceptó:** el código generado, tras leer el diff completo.
- **Qué se descartó y por qué:** nada se descartó.
- **Cómo se verificó:** `mvn verify` de web y recorrido en el navegador con los usuarios de demostración.

## 2026-11-25 · Página de aterrizaje del conjunto (modificación aparte, a pedido del docente)

- **Herramienta:** Claude (asistente de IA).
- **Tarea:** modificación aparte, sin PBI, sin puntos y sin tarea T### · **Responsable:** Felix José Navarro Ortiz
- **Qué se pidió:** una página de aterrizaje en la web, con el nombre del conjunto, el acceso, lo que hace cada rol, el contacto del equipo y modo claro y oscuro. Se escogió la opción C (entrada por roles) entre tres propuestas de diseño, y se quitó el nombre HabitaGest de la página.
- **Archivos generados:** web/…/templates/aterrizaje.html, ControladorInicio.java, seguridad/SeguridadConfig.java, templates/fragmentos.html (título de la pestaña) y AterrizajeTest.java.
- **Qué se aceptó:** el código y las pruebas generados, tras leer el diff completo. Sin sesión, `/` muestra la página de aterrizaje; con sesión muestra las opciones del rol. `/login` redirige a `/`.
- **Qué se descartó y por qué:** la plantilla login.html queda sin uso y no se borró.
- **Cómo se verificó:** `mvn verify` de web (AterrizajeTest, 3 pruebas) y recorrido en el navegador: modo claro y oscuro, clave incorrecta, ingreso y salida.

