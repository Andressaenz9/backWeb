package com.finca.arriendo.security;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Buscando usuario con nombre: " + username);

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByNombre(username);

        if (usuario == null) {
            System.err.println("Usuario no encontrado en la base de datos: " + username);
            throw new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username);
        }

        System.out.println("Usuario encontrado: " + usuario.getNombre() + " con rol: ROLE_" + usuario.getTipo().name());

        // Devolver los detalles del usuario con el rol correspondiente
        return new User(
                usuario.getNombre(),
                usuario.getContrasena(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getTipo().name()))
        );
    }
}
