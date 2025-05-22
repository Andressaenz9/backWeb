package com.finca.arriendo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.UsuarioRepository;
import com.finca.arriendo.security.UserTokenService;

@Service
public class AuthService {
  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private UserTokenService userTokenService;
  
  //Metodo para autenticar al usuario y generar el token
  public String autenticarYGenerarToken(String nombre, String contrasena) {
    System.out.println("Autenticando usuario: " + nombre);
    Usuario usuario = usuarioRepository.findByNombre(nombre);

    if (usuario == null) {
        throw new RuntimeException("Usuario no encontrado");
    }

    if (!usuario.getContrasena().equals(contrasena)) {
        throw new RuntimeException("Contraseña incorrecta");
    }
    String token = userTokenService.generateUserToken(usuario);
    System.out.println("Token generado para el usuario " + nombre + ": " + token);
    return token;
  }
}
