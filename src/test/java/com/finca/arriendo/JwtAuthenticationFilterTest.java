package com.finca.arriendo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.finca.arriendo.security.CustomUserDetailsService;
import com.finca.arriendo.security.JwtAuthenticationFilter;
import com.finca.arriendo.security.UserTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private UserTokenService userTokenService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Jws<Claims> jwsClaims;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(userTokenService, customUserDetailsService);
    }

    @Test
    public void testDoFilterInternal_ValidToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(userTokenService.parseToken("validToken")).thenReturn(jwsClaims);
        when(jwsClaims.getBody().getSubject()).thenReturn("testUser");

        UserDetails userDetails = mock(UserDetails.class);
        when(customUserDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

        jwtAuthenticationFilter.doFilterForTest(request, response, filterChain);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) securityContext.getAuthentication();

        assertEquals(userDetails, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
public void testDoFilterInternal_InvalidToken() throws Exception {
    // Proporcionar un token con formato válido pero firmado incorrectamente
    String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsIm5hbWUiOiJKb2huIERvZSIsImlhdCI6MTUxNjIzOTAyMn0.invalidSignature";

    when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

    // Simular la excepción que arroja el servicio al procesar el token inválido
    when(userTokenService.parseToken(invalidToken)).thenThrow(
        new UserTokenService.TokenValidationException("ERROR: Token inválido o expirado", null)
    );

    // Ejecutar el filtro
    jwtAuthenticationFilter.doFilterForTest(request, response, filterChain);

    // Verificar que la respuesta tenga un estado 401
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(response.getWriter()).write("Token inválido o expirado");
}


}
