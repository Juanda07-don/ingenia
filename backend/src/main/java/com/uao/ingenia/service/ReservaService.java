package com.uao.ingenia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uao.ingenia.dto.AsesoriaDTO;
import com.uao.ingenia.exception.HorarioOcupadoException;
import com.uao.ingenia.exception.OperacionInvalidaException;
import com.uao.ingenia.exception.RecursoNoEncontradoException;
import com.uao.ingenia.mapper.AsesoriaMapper;
import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.Disponibilidad;
import com.uao.ingenia.model.EstadoAsesoria;
import com.uao.ingenia.model.EstadoDisponibilidad;
import com.uao.ingenia.model.Estudiante;
import com.uao.ingenia.repository.AsesoriaRepository;
import com.uao.ingenia.repository.DisponibilidadRepository;
import com.uao.ingenia.repository.EstudianteRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservaService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final AsesoriaRepository asesoriaRepository;
    private final EstudianteRepository estudianteRepository;
    private final NotificacionService notificacionService;
    private final ReglasDeTiempo reglasDeTiempo;

    public ReservaService(DisponibilidadRepository disponibilidadRepository,
                        AsesoriaRepository asesoriaRepository,
                        EstudianteRepository estudianteRepository,
                        NotificacionService notificacionService,
                        ReglasDeTiempo reglasDeTiempo) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.asesoriaRepository = asesoriaRepository;
        this.estudianteRepository = estudianteRepository;
        this.notificacionService = notificacionService;
        this.reglasDeTiempo = reglasDeTiempo;
        
    }

    @Transactional
    public AsesoriaDTO reservar(Long estudianteId, Long disponibilidadId) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));

        Disponibilidad d = disponibilidadRepository.buscarParaReservar(disponibilidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado"));

        if (d.getEstado() != EstadoDisponibilidad.LIBRE) {
            throw new HorarioOcupadoException();
        }

        if (reglasDeTiempo.yaPaso(d.getFecha(), d.getHoraInicio())) {      
            throw new OperacionInvalidaException("Ese horario ya pasó");   
        }

        d.setEstado(EstadoDisponibilidad.OCUPADA);

        Asesoria a = new Asesoria();
        a.setEstudiante(estudiante);
        a.setProfesor(d.getProfesor());
        a.setMateria(d.getMateria());
        a.setDisponibilidad(d);
        a.setFecha(d.getFecha());
        a.setHoraInicio(d.getHoraInicio());
        a.setHoraFin(d.getHoraFin());
        a.setEstado(EstadoAsesoria.AGENDADA);
        a = asesoriaRepository.save(a);

        notificacionService.avisarProfesorNuevaReserva(a);
        return AsesoriaMapper.aDTO(a);
    }

    public List<AsesoriaDTO> asesoriasDelEstudiante(Long estudianteId) {
        return asesoriaRepository
                .findByEstudiante_IdUsuarioOrderByFechaDescHoraInicioDesc(estudianteId)
                .stream().map(AsesoriaMapper::aDTO).toList();}

    public List<AsesoriaDTO> asesoriasDelProfesor(Long profesorId) {
    return asesoriaRepository
            .findByProfesor_IdUsuarioOrderByFechaAscHoraInicioAsc(profesorId)
            .stream().map(AsesoriaMapper::aDTO).toList();
        }
    }

