package com.ProyectoFinal;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

// Registra vistas que no necesitan lógica de un @Controller: simplemente
// mapean una URL a una plantilla Thymeleaf (ciclo de vida de una petición:
// DispatcherServlet -> View Resolver, visto en la semana 1 del curso).
@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(
            ViewControllerRegistry registry
    ) {

        // El formulario de login (el envío del POST lo procesa Spring Security)
        registry.addViewController("/login")
                .setViewName("auth/login");

        // Pantalla pública de "acceso" (elegir entre iniciar sesión o registrarse)
        registry.addViewController("/acceso")
                .setViewName("auth/acceso");

        // Página mostrada cuando Spring Security deniega el acceso (403)
        registry.addViewController("/403")
                .setViewName("auth/403");
    }
}
