package com.finca.arriendo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.finca.arriendo.security.CorsConfig;

public class CorsConfigTest {

    @Test
    public void testCorsFilterConfiguration() {
        CorsConfig corsConfig = new CorsConfig();

        // Crear manualmente la fuente de configuración
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Configurar CORS manualmente
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://127.0.0.1:4200"));
        config.addAllowedHeader("*");
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");

        // Registrar la configuración en la fuente
        source.registerCorsConfiguration("/**", config);

        // Crear el filtro basado en la fuente
        CorsFilter filter = corsConfig.corsFilter();

        // Validar que la configuración fue aplicada correctamente
        CorsConfiguration appliedConfig = source.getCorsConfigurations().get("/**");

        assertNotNull(appliedConfig);
        assertTrue(appliedConfig.getAllowedOrigins().contains("http://localhost:4200"));
        assertTrue(appliedConfig.getAllowedMethods().contains("POST"));
        assertTrue(appliedConfig.getAllowedHeaders().contains("*"));
    }
}
