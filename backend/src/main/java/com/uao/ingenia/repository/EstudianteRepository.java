package com.uao.ingenia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
}