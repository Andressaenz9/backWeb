package com.finca.arriendo.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserTokenService userTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(UserTokenService userTokenService, CustomUserDetailsService customUserDetailsService) {
        this.userTokenService = userTokenService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "*");

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Encabezado Authorization no presente o no comienza con 'Bearer'.");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7); // Eliminar "Bearer "
        System.out.println("Token recibido: " + token);

        try {
            // Validar el token y extraer los claims
            Jws<Claims> claims = UserTokenService.parseToken(token);
            String username = claims.getBody().getSubject();
            System.out.println("Usuario extraído del token: " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Buscar detalles del usuario
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                System.out.println("Detalles del usuario encontrados: " + userDetails.getUsername());

                // Establecer autenticación en el contexto de seguridad
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("Autenticación establecida para: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error al parsear el token: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token inválido o expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // Método público para pruebas
    public void doFilterForTest(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        doFilterInternal(request, response, filterChain);
    }
}
