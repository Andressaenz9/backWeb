package com.finca.arriendo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.finca.arriendo.model.Tipo;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.UsuarioRepository;
import com.finca.arriendo.security.CustomUserDetailsService;

public class CustomUserDetailsServiceTest {

    private CustomUserDetailsService userDetailsService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
public void setUp() {
    MockitoAnnotations.openMocks(this);
    userDetailsService = new CustomUserDetailsService(usuarioRepository); // Inyectar el mock
}


    @Test
    public void testLoadUserByUsername_Success() {
        Usuario usuario = new Usuario();
        usuario.setNombre("testUser");
        usuario.setContrasena("password");
        usuario.setTipo(Tipo.ARRENDADOR);

        when(usuarioRepository.findByNombre("testUser")).thenReturn(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testUser");

        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ARRENDADOR")));
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        when(usuarioRepository.findByNombre("nonexistentUser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistentUser");
        });
    }
}
