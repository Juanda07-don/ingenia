package com.uao.ingenia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Profesor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    // Buscar por nombre del profesor (RF-01)
    List<Profesor> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por materia: profesores que la dictan (RF-02)
    List<Profesor> findDistinctByMaterias_NombreContainingIgnoreCase(String materia);
}
