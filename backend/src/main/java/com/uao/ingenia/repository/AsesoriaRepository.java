package com.uao.ingenia.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.EstadoAsesoria;

public interface AsesoriaRepository extends JpaRepository<Asesoria, Long> {
    List<Asesoria> findByEstudiante_IdUsuarioOrderByFechaDescHoraInicioDesc(Long estudianteId);
    List<Asesoria> findByProfesor_IdUsuarioOrderByFechaAscHoraInicioAsc(Long profesorId);
    boolean existsByDisponibilidad_IdDisponibilidad(Long idDisponibilidad);
    boolean existsByProfesor_IdUsuarioAndFechaAndEstadoAndHoraInicioLessThan(
        Long profesorId, LocalDate fecha, EstadoAsesoria estado, LocalTime horaInicio);

    Optional<Asesoria> findFirstByProfesor_IdUsuarioAndFechaAndEstadoOrderByHoraInicioAsc(
        Long profesorId, LocalDate fecha, EstadoAsesoria estado);
}