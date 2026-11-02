package com.uao.ingenia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {
    
}
