package com.finca.arriendo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.finca.arriendo.model.Tipo;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.security.UserTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public class UserTokenServiceTest {

    @Test
    public void testGenerateAndParseToken() {
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@test.com");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setTipo(Tipo.ARRENDADOR);

        String token = UserTokenService.generateUserToken(usuario);
        assertNotNull(token);

        Jws<Claims> claims = UserTokenService.parseToken(token);
        assertEquals("test@test.com", claims.getBody().getSubject());
        assertEquals("Test", claims.getBody().get("nombre"));
    }

    @Test
    public void testIsTokenExpired() {
        String expiredToken = "TOKEN_EXPIRADO"; // Proporcionar un token expirado
        assertThrows(UserTokenService.TokenValidationException.class, () -> {
            UserTokenService.isTokenExpired(expiredToken);
        });
    }
}
