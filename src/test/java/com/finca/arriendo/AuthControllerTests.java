package com.finca.arriendo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finca.arriendo.dto.LoginRequest;
import com.finca.arriendo.services.AuthService;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    private LoginRequest loginRequest;

    @BeforeEach
    public void setup() {
        // Preparamos los datos para las pruebas
        loginRequest = new LoginRequest();
        loginRequest.setNombre("UserTest");
        loginRequest.setContrasena("passwordTest");
    }

    @Test
    public void testLogin_Success() throws Exception {
        // Simula la respuesta del servicio AuthService
        String expectedToken = "tokenGenerado";
        when(authService.autenticarYGenerarToken(loginRequest.getNombre(), loginRequest.getContrasena()))
                .thenReturn(expectedToken);

        // Realiza la solicitud POST a la ruta /auth/login y verifica que se retorna el token
        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest)) // Convierte el DTO a JSON
                        .contentType(MediaType.APPLICATION_JSON)) // Cambia el tipo de contenido a JSON
                .andExpect(MockMvcResultMatchers.status().isOk()) // Código de estado 200 OK
                .andExpect(MockMvcResultMatchers.content().string(expectedToken)); // Verifica que el token sea el esperado
    }

    @Test
    public void testLogin_Failure() throws Exception {
        // Simula el caso de error en la autenticación
        when(authService.autenticarYGenerarToken(loginRequest.getNombre(), loginRequest.getContrasena()))
                .thenReturn(null);

        // Realiza la solicitud y verifica que se retorne un error
        mockMvc.perform(post("/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest)) // Convierte el DTO a JSON
                        .contentType(MediaType.APPLICATION_JSON)) // Cambia el tipo de contenido a JSON
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()); // Espera un 401 si el login falla
    }
}
