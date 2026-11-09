package com.habitagest.web.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/** REQ-PH-001 y REQ-PH-003: cada rol entra solo a su área; las demás devuelven 403. */
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain cadenaDeSeguridad(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(acceso -> {
            acceso.requestMatchers("/login", "/403", "/error", "/actuator/health").permitAll();
            for (Rol rol : Rol.values()) {
                acceso.requestMatchers(rol.rutaBase(), rol.rutaBase() + "/**").hasRole(rol.name());
            }
            acceso.anyRequest().authenticated();
        });
        http.formLogin(formulario -> formulario.loginPage("/login").defaultSuccessUrl("/", true).permitAll());
        http.logout(salida -> salida.logoutSuccessUrl("/login?salio").permitAll());
        http.exceptionHandling(errores -> errores.accessDeniedPage("/403"));
        return http.build();
    }
}
