package com.finca.arriendo.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permitir todas las credenciales (cookies, sesiones, etc.)
        config.setAllowCredentials(true);

        // Permitir todos los orígenes
        config.setAllowedOriginPatterns(Arrays.asList("http://localhost:4200"));

        // Permitir todos los encabezados
        config.addAllowedHeader("*");

        // Permitir todos los métodos HTTP
        config.addAllowedMethod("Authorization");

        // Exponer todas las cabeceras necesarias
        config.addExposedHeader("*");
        config.addExposedHeader("Content-Type");

        // Registrar la configuración para todas las rutas
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
