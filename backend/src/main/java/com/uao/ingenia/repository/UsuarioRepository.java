package com.uao.ingenia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreoInstitucional(String correo);
}
