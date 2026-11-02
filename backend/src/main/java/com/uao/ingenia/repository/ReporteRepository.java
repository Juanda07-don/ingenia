package com.uao.ingenia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Reporte;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    Optional<Reporte> findByAsesoria_IdAsesoria(Long idAsesoria);
}
