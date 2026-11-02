package com.uao.ingenia.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uao.ingenia.dto.LoginRequest;
import com.uao.ingenia.dto.LoginResponse;
import com.uao.ingenia.service.AutenticacionService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return autenticacionService.iniciarSesion(request.correo(), request.contrasena());
    }
}
