package com.uao.ingenia.service;

import com.uao.ingenia.dto.ConfigurarEspaciosRequest;
import com.uao.ingenia.dto.EspacioDTO;
import com.uao.ingenia.exception.OperacionInvalidaException;
import com.uao.ingenia.exception.RecursoNoEncontradoException;
import com.uao.ingenia.model.*;
import com.uao.ingenia.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadService {

    private final ProfesorRepository profesorRepository;
    private final MateriaRepository materiaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final AsesoriaRepository asesoriaRepository;
    private final ReglasDeTiempo reglasDeTiempo;
    private final int duracionMinutos;
    
    

    public DisponibilidadService(ProfesorRepository profesorRepository,
                                MateriaRepository materiaRepository,
                                DisponibilidadRepository disponibilidadRepository,
                                AsesoriaRepository asesoriaRepository,
                                ReglasDeTiempo reglasDeTiempo,
                                @Value("${ingenia.asesoria.duracion-minutos}") int duracionMinutos) {
        this.profesorRepository = profesorRepository;
        this.materiaRepository = materiaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.reglasDeTiempo = reglasDeTiempo;
        this.duracionMinutos = duracionMinutos;
    }

    @Transactional
    public List<EspacioDTO> crearEspacios(Long profesorId, ConfigurarEspaciosRequest r) {
        if (r.materiaId() == null || r.fecha() == null || r.horaInicio() == null || r.horaFin() == null) {
            throw new OperacionInvalidaException("Faltan datos para configurar la disponibilidad");
        }

        Profesor profesor = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));
        Materia materia = materiaRepository.findById(r.materiaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia no encontrada"));

        boolean dictaLaMateria = profesor.getMaterias().stream()
                .anyMatch(m -> m.getIdMateria().equals(materia.getIdMateria()));
        if (!dictaLaMateria) {
            throw new OperacionInvalidaException("No tienes asignada esa materia");
        }

        if (reglasDeTiempo.yaPaso(r.fecha(), r.horaInicio())) {
            throw new OperacionInvalidaException("No puedes configurar espacios en un horario que ya pasó");
        }
        if (profesor.getCubiculo() == null || profesor.getCubiculo().isBlank()) {
            throw new OperacionInvalidaException("No tienes un cubículo registrado; contacta a la facultad");
        }
        if (!r.horaFin().isAfter(r.horaInicio())) {
            throw new OperacionInvalidaException("La hora de fin debe ser posterior a la de inicio");
        }
        long minutos = Duration.between(r.horaInicio(), r.horaFin()).toMinutes();
        if (minutos % duracionMinutos != 0) {
            throw new OperacionInvalidaException("El rango debe ser múltiplo de " + duracionMinutos + " minutos");
        }
        if (disponibilidadRepository
                .existsByProfesor_IdUsuarioAndFechaAndHoraInicioLessThanAndHoraFinGreaterThan(
                        profesorId, r.fecha(), r.horaFin(), r.horaInicio())) {
            throw new OperacionInvalidaException("Ya tienes espacios configurados en ese horario");
        }

        List<Disponibilidad> espacios = new ArrayList<>();
        LocalTime inicio = r.horaInicio();
        while (inicio.isBefore(r.horaFin())) {
            LocalTime fin = inicio.plusMinutes(duracionMinutos);
            Disponibilidad d = new Disponibilidad();
            d.setProfesor(profesor);
            d.setMateria(materia);
            d.setFecha(r.fecha());
            d.setHoraInicio(inicio);
            d.setHoraFin(fin);
            d.setEstado(EstadoDisponibilidad.LIBRE);
            espacios.add(d);
            inicio = fin;
        }
        return disponibilidadRepository.saveAll(espacios).stream().map(this::aDTO).toList();
    }

    public List<EspacioDTO> listarEspacios(Long profesorId, LocalDate fecha) {
        return disponibilidadRepository
                .findByProfesor_IdUsuarioAndFechaOrderByHoraInicio(profesorId, fecha)
                .stream().map(this::aDTO).toList();
    }

    @Transactional
    public void eliminarEspacio(Long profesorId, Long espacioId) {
        Disponibilidad d = disponibilidadRepository.buscarParaReservar(espacioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado"));

        if (!d.getProfesor().getIdUsuario().equals(profesorId)) {
            throw new OperacionInvalidaException("Este espacio no es tuyo");
        }
        if (d.getEstado() != EstadoDisponibilidad.LIBRE) {
            throw new OperacionInvalidaException("No puedes quitar un espacio que ya está reservado");
        }
        if (asesoriaRepository.existsByDisponibilidad_IdDisponibilidad(espacioId)) {
            throw new OperacionInvalidaException("Este espacio ya tiene asesorías en su historial y no se puede quitar");
        }
        disponibilidadRepository.delete(d);
    }

    private EspacioDTO aDTO(Disponibilidad d) {
        return new EspacioDTO(d.getIdDisponibilidad(), d.getMateria().getNombre(), d.getFecha(),
                d.getHoraInicio(), d.getHoraFin(), d.getProfesor().getCubiculo(), d.getEstado().name());
    }
}
