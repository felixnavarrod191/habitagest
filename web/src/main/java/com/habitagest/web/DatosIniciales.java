package com.habitagest.web;

import com.habitagest.web.seguridad.Rol;
import com.habitagest.web.usuarios.Usuario;
import com.habitagest.web.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea el administrador inicial con la contraseña de ADMIN_PASSWORD (nunca escrita en el código) y,
 * con APP_DEMO=true, un usuario ficticio por rol con la contraseña de DEMO_PASSWORD (NFR-PH-004).
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    static final String NOMBRE_ADMIN = "Administrador del sistema";
    static final String DOCUMENTO_ADMIN = "1000000001";

    private final UsuarioRepository repositorio;
    private final PasswordEncoder codificador;
    private final String contrasenaAdmin;
    private final boolean demo;
    private final String contrasenaDemo;

    public DatosIniciales(UsuarioRepository repositorio, PasswordEncoder codificador,
            @Value("${ADMIN_PASSWORD:}") String contrasenaAdmin,
            @Value("${APP_DEMO:false}") boolean demo,
            @Value("${DEMO_PASSWORD:}") String contrasenaDemo) {
        this.repositorio = repositorio;
        this.codificador = codificador;
        this.contrasenaAdmin = contrasenaAdmin;
        this.demo = demo;
        this.contrasenaDemo = contrasenaDemo;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() == 0) {
            if (contrasenaAdmin.isBlank()) {
                throw new IllegalStateException(
                        "Falta la variable de entorno ADMIN_PASSWORD para crear el administrador inicial.");
            }
            repositorio.save(conDatos(new Usuario("admin", codificador.encode(contrasenaAdmin), Rol.ADMIN_SISTEMA, true),
                    NOMBRE_ADMIN, DOCUMENTO_ADMIN));
        }
        if (demo) {
            if (contrasenaDemo.isBlank()) {
                throw new IllegalStateException("Falta la variable de entorno DEMO_PASSWORD para los usuarios de demostración.");
            }
            for (Rol rol : Rol.values()) {
                String nombre = "demo_" + rol.name().toLowerCase();
                if (!repositorio.existsByUsuario(nombre)) {
                    repositorio.save(conDatos(new Usuario(nombre, codificador.encode(contrasenaDemo), rol, true),
                            nombreDemo(rol), documentoDemo(rol)));
                }
            }
        }
        completarDatosFaltantes();
    }

    /** Todo usuario lleva nombre y documento: al administrador y a los de demostración creados antes se los completa. */
    private void completarDatosFaltantes() {
        for (Usuario existente : repositorio.findAll()) {
            if (existente.getNombreCompleto() != null && !existente.getNombreCompleto().isBlank()) {
                continue;
            }
            if ("admin".equals(existente.getUsuario())) {
                repositorio.save(conDatos(existente, NOMBRE_ADMIN, DOCUMENTO_ADMIN));
            } else if (existente.getUsuario() != null && existente.getUsuario().startsWith("demo_") && existente.getRol() != null) {
                repositorio.save(conDatos(existente, nombreDemo(existente.getRol()), documentoDemo(existente.getRol())));
            }
        }
    }

    static String nombreDemo(Rol rol) {
        return "Demo " + rol.etiqueta();
    }

    static String documentoDemo(Rol rol) {
        return String.valueOf(9000000 + rol.ordinal() + 1);
    }

    private static Usuario conDatos(Usuario usuario, String nombreCompleto, String documento) {
        usuario.setNombreCompleto(nombreCompleto);
        usuario.setDocumento(documento);
        return usuario;
    }
}
