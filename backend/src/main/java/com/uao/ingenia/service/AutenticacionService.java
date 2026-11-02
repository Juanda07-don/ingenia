package com.uao.ingenia.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uao.ingenia.dto.LoginResponse;
import com.uao.ingenia.exception.CredencialesInvalidasException;
import com.uao.ingenia.model.Profesor;
import com.uao.ingenia.model.Usuario;
import com.uao.ingenia.repository.UsuarioRepository;

@Service
public class AutenticacionService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    

    public AutenticacionService(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse iniciarSesion(String correo, String contrasena) {
        if (correo == null || contrasena == null) {
            throw new CredencialesInvalidasException();
        }
        String correoNormalizado = correo.trim().toLowerCase();

        // Regla de negocio de CU-01: solo correos institucionales
        if (!correoNormalizado.endsWith("@uao.edu.co")) {
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = usuarioRepository.findByCorreoInstitucional(correoNormalizado)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new CredencialesInvalidasException();
        }

        String rol = (usuario instanceof Profesor) ? "PROFESOR" : "ESTUDIANTE";
        return new LoginResponse(usuario.getIdUsuario(), usuario.getNombre(), rol);
    }
}