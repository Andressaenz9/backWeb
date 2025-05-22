package com.finca.arriendo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finca.arriendo.dto.LoginRequest;
import com.finca.arriendo.services.AuthService;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    try {
        System.out.println("Intento de inicio de sesión para: " + loginRequest.getNombre());
        String token = authService.autenticarYGenerarToken(loginRequest.getNombre(), loginRequest.getContrasena());
        if (token == null) {
            System.err.println("Credenciales incorrectas para: " + loginRequest.getNombre());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }
        System.out.println("Token generado correctamente: " + token);
        return ResponseEntity.ok(token);

    } catch (RuntimeException e) {
        if (e.getMessage().equals("Usuario no encontrado")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } else if (e.getMessage().equals("Contraseña incorrecta")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
}

}
