package com.finca.arriendo.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.finca.arriendo.model.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class UserTokenService {
    // Clave en texto plano (puedes usar una clave fija o generarla dinámicamente)
    private static final String SECRET_KEY_STRING = "TuClaveSecretaMuySeguraDeAlMenos32Caracteres1";

    // Conversión de la clave a formato Base64
    private static final String BASE64_SECRET_KEY = Base64.getEncoder().encodeToString(SECRET_KEY_STRING.getBytes());

    // Crear la clave secreta a partir de la clave Base64
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(BASE64_SECRET_KEY));

    private static final long EXPIRATION_TIME = 3600000; // 1 hora en milisegundos

    // Método para generar el token JWT
    public static String generateUserToken(Usuario usuario) {
        if (usuario == null || usuario.getCorreo() == null) {
            throw new IllegalArgumentException("El usuario o su correo no puede ser nulo");
        }
        String token = Jwts.builder()
                .setSubject(usuario.getCorreo())
                .claim("nombre", usuario.getNombre())
                .claim("apellido", usuario.getApellido())
                .claim("tipo_usuario", usuario.getTipo().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
        return token;
    }

    // Método para parsear el token y obtener los Claims
    public static Jws<Claims> parseToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            System.out.println(token);
            //System.out.println(SECRET_KEY);
            System.out.println("EL TOKEN TA BIEN");
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("ESTA MONDA FALLO EN PARSE TOKEN");
            throw new TokenValidationException("ERROR: Token inválido o expirado", e);
        }
    }

    // Método para verificar si el token está expirado
    public static boolean isTokenExpired(String token) {
        System.out.println(token);
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            boolean isExpired = claims.getBody().getExpiration().before(new Date());
            return isExpired;
        } catch (JwtException e) {
            throw new TokenValidationException("Token invAlido o expirado", e);
        }
    }

    // Excepción personalizada para manejo de errores de validación de tokens
    public static class TokenValidationException extends JwtException {
        public TokenValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
