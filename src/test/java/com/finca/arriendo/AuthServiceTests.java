package com.finca.arriendo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.finca.arriendo.model.Tipo;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.UsuarioRepository;
import com.finca.arriendo.security.UserTokenService;
import com.finca.arriendo.services.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UserTokenService userTokenService;

    @InjectMocks
    private AuthService authService;

    private Usuario testUsuario;

    @BeforeEach
    public void setUp() {
        // Configuración de un usuario de prueba
        testUsuario = new Usuario();
        testUsuario.setId(1L);
        testUsuario.setNombre("usuarioTest");
        testUsuario.setApellido("apellidoTest");
        testUsuario.setCorreo("usuario@example.com");
        testUsuario.setContrasena("password123");
        testUsuario.setTipo(Tipo.ARRENDADOR); // Tipo de usuario
    }

    @Test
    public void testAutenticarYGenerarToken_Exito() {
        // Simulación del repositorio devolviendo el usuario de prueba
        when(usuarioRepository.findByNombre("usuarioTest")).thenReturn(testUsuario);

        // Asegurarse de que el token sea generado por un mock del servicio
        when(userTokenService.generateUserToken(any(Usuario.class)))
            .thenReturn("dummy.token.generated");

        // Ejecutamos el método de autenticación y generación de token
        String token = authService.autenticarYGenerarToken("usuarioTest", "password123");

        // Verificamos que el token generado no sea nulo y tenga el formato correcto
        assertNotNull(token);
        assertEquals("dummy.token.generated", token);
    }






    @Test
    public void testAutenticarYGenerarToken_FalloPorCredencialesIncorrectas() {
        // Simulación del repositorio devolviendo el usuario correcto
        when(usuarioRepository.findByNombre("usuarioTest")).thenReturn(testUsuario);

        // Problemas de autenticación con una contraseña incorrecta
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.autenticarYGenerarToken("usuarioTest", "wrongPassword");
        });

        // Verificar el mensaje de error
        assertEquals("Contraseña incorrecta", exception.getMessage());
    }

    @Test
    public void testAutenticarYGenerarToken_FalloPorUsuarioNoExistente() {
        // Simular que el repositorio no encuentra al usuario
        when(usuarioRepository.findByNombre("nonexistentUser")).thenReturn(null);

        // Problemas de autenticación con un usuario inexistente
        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.autenticarYGenerarToken("nonexistentUser", "password123");
        });

        // Verificar el mensaje de error
        assertEquals("Usuario no encontrado", exception.getMessage());
    }
}
