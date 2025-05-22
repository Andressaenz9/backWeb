package com.finca.arriendo;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.finca.arriendo.services.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
public class WebSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Cambiado de @Autowired a @MockBean para simular el servicio
    private AuthService authService;

    @Test
    public void testPermitAllForLogin() throws Exception {
        String expectedToken = "tokenGenerado";

        // Simula el comportamiento del método autenticarYGenerarToken
        when(authService.autenticarYGenerarToken("testUser", "password")).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\": \"testUser\", \"contrasena\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedToken));
    }

    @Test
    public void testDenyAccessForAuthenticatedRoutes() throws Exception {
        // Verifica que las rutas protegidas devuelven 401 cuando no hay autenticación
        mockMvc.perform(post("/admin/some-protected-route"))
               .andExpect(status().isUnauthorized());
    }
}
