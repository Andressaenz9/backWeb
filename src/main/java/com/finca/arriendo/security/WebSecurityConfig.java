package com.finca.arriendo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import jakarta.servlet.http.HttpServletResponse;


@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(request -> {
                var config = new org.springframework.web.cors.CorsConfiguration();
                config.addAllowedOrigin("*"); // Origen permitido
                config.addAllowedMethod("*");
                config.addAllowedHeader("*"); // Encabezados permitidos
                config.setAllowCredentials(false); // Permitir credenciales
                return config;
            }))
            .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().permitAll()
        )
            .csrf(csrf -> csrf
                .disable()  // Deshabilitar CSRF para permitir el acceso a la consola H2
            )
            .headers(headers -> 
                headers.frameOptions(frameOptions -> frameOptions.sameOrigin())  // Permitir iframe de H2 en la misma origen
            )
            .exceptionHandling(exceptionHandling -> 
                exceptionHandling.authenticationEntryPoint(unauthorizedEntryPoint()) // Configurar punto de entrada
            )
            // Configuración de logout (opcional si es necesario)
            .logout(logout -> 
                logout.logoutUrl("/auth/logout")  // URL de logout personalizada
                      .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())  // Configuración de éxito en el logout
            );

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
    return (request, response, authException) -> {
        System.out.println("Acceso no autorizado DEL TOKEN: " + request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    };
}

}
