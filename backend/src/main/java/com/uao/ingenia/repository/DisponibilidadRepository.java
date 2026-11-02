package com.uao.ingenia.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uao.ingenia.model.Disponibilidad;
import com.uao.ingenia.model.EstadoDisponibilidad;

import jakarta.persistence.LockModeType;


public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    // Espacios libres de un profesor en un día (RF-03)
    List<Disponibilidad> findByProfesor_IdUsuarioAndFechaAndEstadoOrderByHoraInicio(
            Long profesorId, LocalDate fecha, EstadoDisponibilidad estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Disponibilidad d where d.idDisponibilidad = :id")
    Optional<Disponibilidad> buscarParaReservar(@Param("id") Long id);

    List<Disponibilidad> findByProfesor_IdUsuarioAndFechaOrderByHoraInicio(Long profesorId, LocalDate fecha);

    boolean existsByProfesor_IdUsuarioAndFechaAndHoraInicioLessThanAndHoraFinGreaterThan(
    Long profesorId, LocalDate fecha, LocalTime horaFin, LocalTime horaInicio);

    @Query("""
        select d from Disponibilidad d
        where d.profesor.idUsuario = :profesorId
        and d.estado = :estado
        and (d.fecha > :hoy or (d.fecha = :hoy and d.horaInicio > :ahora))
        order by d.fecha, d.horaInicio
        """)
List<Disponibilidad> proximosLibres(@Param("profesorId") Long profesorId,
                                    @Param("estado") EstadoDisponibilidad estado,
                                    @Param("hoy") LocalDate hoy,
                                    @Param("ahora") LocalTime ahora,
                                    Pageable pageable);
}