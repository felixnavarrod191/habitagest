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
            repositorio.save(new Usuario("admin", codificador.encode(contrasenaAdmin), Rol.ADMIN_SISTEMA, true));
        }
        if (demo) {
            if (contrasenaDemo.isBlank()) {
                throw new IllegalStateException("Falta la variable de entorno DEMO_PASSWORD para los usuarios de demostración.");
            }
            for (Rol rol : Rol.values()) {
                String nombre = "demo_" + rol.name().toLowerCase();
                if (!repositorio.existsByUsuario(nombre)) {
                    repositorio.save(new Usuario(nombre, codificador.encode(contrasenaDemo), rol, true));
                }
            }
        }
    }
}
