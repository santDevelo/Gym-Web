package com.ProyectoFinal;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(
            ViewControllerRegistry registry
    ) {

        registry.addViewController("/login")
                .setViewName("auth/login");

        registry.addViewController("/acceso")
                .setViewName("auth/acceso");

        registry.addViewController("/403")
                .setViewName("auth/403");
    }
    
    @Bean
    public LocaleResolver localeResolver() {

        SessionLocaleResolver resolver =
                new SessionLocaleResolver();

        resolver.setDefaultLocale(
                Locale.forLanguageTag("es-CR")
        );

        resolver.setLocaleAttributeName(
                "session.current.locale"
        );

        resolver.setTimeZoneAttributeName(
                "session.current.timezone"
        );

        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {

        LocaleChangeInterceptor interceptor =
                new LocaleChangeInterceptor();

        interceptor.setParamName("lang");

        return interceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry
    ) {

        registry.addInterceptor(
                localeChangeInterceptor()
        );
    }

    @Bean("messageSource")
    public MessageSource messageSource() {

        ResourceBundleMessageSource messageSource =
                new ResourceBundleMessageSource();

        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");

        return messageSource;
    }
    

}