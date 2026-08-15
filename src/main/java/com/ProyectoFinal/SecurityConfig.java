package com.ProyectoFinal;

import com.ProyectoFinal.domain.Ruta;
import com.ProyectoFinal.service.RutaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Configuración de Spring Security. A diferencia de las primeras clases de
// seguridad (URLs "PUBLIC_URLS"/"ADMIN_URLS" hardcodeadas en constantes), acá
// las reglas de acceso se arman dinámicamente leyendo la tabla "ruta" de la
// BD: agregar o cambiar una regla no requiere tocar código, solo la BD.
@Configuration
public class SecurityConfig {

    // @Lazy en RutaService evita una dependencia circular al construir este
    // bean (RutaService -> RutaRepository -> ... -> beans de seguridad)
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Lazy RutaService rutaService
    ) throws Exception {

        var rutas = rutaService.getRutas();

        // Recorre cada fila de "ruta": si requiere rol, exige ese rol
        // (hasRole añade el prefijo "ROLE_" automáticamente); si no, es pública
        http.authorizeHttpRequests(requests -> {

            for (Ruta ruta : rutas) {

                if (ruta.isRequiereRol()) {

                    requests
                            .requestMatchers(ruta.getRuta())
                            .hasRole(
                                    ruta.getRol().getRol()
                            );

                } else {

                    requests
                            .requestMatchers(ruta.getRuta())
                            .permitAll();
                }
            }

            // Cualquier URL que no esté en la tabla "ruta" exige estar autenticado
            requests.anyRequest().authenticated();
        });

        // Login por formulario propio (vista "/login"), redirige a /dashboard
        // al entrar bien y de vuelta a /login con ?error=true si falla
        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl(
                        "/dashboard",
                        true
                )
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl(
                        "/login?logout=true"
                )
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
        );

        // Si un usuario autenticado entra a una ruta de otro rol, ve /403
        // en vez del error crudo de Spring Security
        http.exceptionHandling(exceptions ->
                exceptions.accessDeniedPage("/403")
        );

        // Una sola sesión activa por usuario a la vez
        http.sessionManagement(session ->
                session.maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
        );

        return http.build();
    }

    // Bean usado tanto para cifrar la contraseña al guardar el usuario como
    // para verificarla en cada login (BCrypt es un hash de una sola vía)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}