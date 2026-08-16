package com.ProyectoFinal;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

// Registra vistas simples que no necesitan lógica de controlador.
@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    private static final String PARAMETRO_IDIOMA = "lang";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Cada ruta se enlaza directamente con su plantilla Thymeleaf.
        registry.addViewController("/login").setViewName("auth/login");
        registry.addViewController("/acceso").setViewName("auth/acceso");
        registry.addViewController("/403").setViewName("auth/403");
    }

    // Conserva en la sesión el idioma elegido y utiliza español inicialmente.
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.forLanguageTag("es"));
        resolver.setLocaleAttributeName("session.current.locale");
        resolver.setTimeZoneAttributeName("session.current.timezone");
        return resolver;
    }

    // Detecta el parámetro ?lang=es, ?lang=en o ?lang=fr en cualquier ruta.
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(PARAMETRO_IDIOMA);
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    // Carga los archivos messages.properties utilizando codificación UTF-8.
    @Bean("messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }
}
