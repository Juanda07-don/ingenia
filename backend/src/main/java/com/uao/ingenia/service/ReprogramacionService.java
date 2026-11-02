package com.uao.ingenia.service;

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
import com.uao.ingenia.repository.AsesoriaRepository;
import com.uao.ingenia.repository.DisponibilidadRepository;

import jakarta.transaction.Transactional;

@Service
public class ReprogramacionService {

    private final AsesoriaRepository asesoriaRepository;
    private final DisponibilidadRepository disponibilidadRepository;
    private final NotificacionService notificacionService;
    private final ReglasDeTiempo reglasDeTiempo;

    public ReprogramacionService(AsesoriaRepository asesoriaRepository,
                                DisponibilidadRepository disponibilidadRepository,
                                NotificacionService notificacionService,
                                ReglasDeTiempo reglasDeTiempo) {
        this.asesoriaRepository = asesoriaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.notificacionService = notificacionService;
        this.reglasDeTiempo = reglasDeTiempo;
    }

    @Transactional
    public AsesoriaDTO reprogramar(Long asesoriaId, Long profesorId,
                                Long nuevaDisponibilidadId, String motivo) {
        Asesoria a = buscar(asesoriaId);

        if (!a.getProfesor().getIdUsuario().equals(profesorId)) {
            throw new OperacionInvalidaException("Solo el profesor asignado puede reprogramar la asesoría");
        }
        if (a.getEstado() != EstadoAsesoria.AGENDADA) {
            throw new OperacionInvalidaException("Solo se puede reprogramar una asesoría agendada");
        }

        Disponibilidad nueva = disponibilidadRepository.buscarParaReservar(nuevaDisponibilidadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Espacio no encontrado"));

        if (!nueva.getProfesor().getIdUsuario().equals(profesorId)) {
            throw new OperacionInvalidaException("El nuevo horario debe ser uno de tus espacios");
        }
        if (nueva.getEstado() != EstadoDisponibilidad.LIBRE) {
            throw new HorarioOcupadoException();
        }
        if (reglasDeTiempo.yaPaso(nueva.getFecha(), nueva.getHoraInicio())) {
            throw new OperacionInvalidaException("El nuevo horario ya pasó");
        }

        nueva.setEstado(EstadoDisponibilidad.OCUPADA);   // se reserva mientras el estudiante decide
        a.setDisponibilidadPropuesta(nueva);
        a.setMotivoReprogramacion(motivo);
        a.setEstado(EstadoAsesoria.REPROGRAMADA);

        notificacionService.avisarEstudianteReprogramacion(a);
        return AsesoriaMapper.aDTO(a);
    }

    @Transactional
    public AsesoriaDTO responder(Long asesoriaId, Long estudianteId, boolean aceptar) {
        Asesoria a = buscar(asesoriaId);

        if (!a.getEstudiante().getIdUsuario().equals(estudianteId)) {
            throw new OperacionInvalidaException("Esta asesoría no te pertenece");
        }
        if (a.getEstado() != EstadoAsesoria.REPROGRAMADA) {
            throw new OperacionInvalidaException("La asesoría no tiene una reprogramación pendiente");
        }

        Disponibilidad original = a.getDisponibilidad();
        Disponibilidad propuesta = a.getDisponibilidadPropuesta();
        original.setEstado(EstadoDisponibilidad.LIBRE);

        if (aceptar) {
            a.setDisponibilidad(propuesta);
            a.setFecha(propuesta.getFecha());
            a.setHoraInicio(propuesta.getHoraInicio());
            a.setHoraFin(propuesta.getHoraFin());
            a.setEstado(EstadoAsesoria.AGENDADA);
            a.setDisponibilidadPropuesta(null);
            a.setMotivoReprogramacion(null);
            notificacionService.avisarEstudianteHorarioAgendado(a);
        } else {
            propuesta.setEstado(EstadoDisponibilidad.LIBRE);
            a.setEstado(EstadoAsesoria.CANCELADA);
            a.setDisponibilidadPropuesta(null);
            notificacionService.avisarProfesorCancelacion(a);
        }
        return AsesoriaMapper.aDTO(a);
    }

    private Asesoria buscar(Long id) {
        return asesoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asesoría no encontrada"));
    }
}
