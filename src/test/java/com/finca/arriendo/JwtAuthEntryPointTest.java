package com.finca.arriendo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

import com.finca.arriendo.security.JwtAuthEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthEntryPointTest {

    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() throws Exception {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this);
        jwtAuthEntryPoint = new JwtAuthEntryPoint();
    }

    @Test
    public void testCommence() throws Exception {
        jwtAuthEntryPoint.commence(request, response, null);

        // Verificar que se envía el error 401
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Unauthorized: Authentication token was either missing or invalid.");
    }
}
