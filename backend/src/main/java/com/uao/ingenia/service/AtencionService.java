package com.uao.ingenia.service;

import com.uao.ingenia.dto.RegistroAtencionRequest;
import com.uao.ingenia.dto.ReporteDTO;
import com.uao.ingenia.exception.OperacionInvalidaException;
import com.uao.ingenia.exception.RecursoNoEncontradoException;
import com.uao.ingenia.mapper.ReporteMapper;
import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.EstadoAsesoria;
import com.uao.ingenia.model.Reporte;
import com.uao.ingenia.repository.AsesoriaRepository;
import com.uao.ingenia.repository.ReporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AtencionService {

    private final AsesoriaRepository asesoriaRepository;
    private final ReporteRepository reporteRepository;
    private final NotificacionService notificacionService;
    private final TurnoService turnoService;
    private final ReglasDeTiempo reglasDeTiempo;

public AtencionService(AsesoriaRepository asesoriaRepository,
                    ReporteRepository reporteRepository,
                    NotificacionService notificacionService,
                    TurnoService turnoService,
                    ReglasDeTiempo reglasDeTiempo) {
    this.asesoriaRepository = asesoriaRepository;
    this.reporteRepository = reporteRepository;
    this.notificacionService = notificacionService;
    this.turnoService = turnoService;
    this.reglasDeTiempo = reglasDeTiempo;
}

    @Transactional
    public ReporteDTO registrarAtencion(Long asesoriaId, RegistroAtencionRequest r) {
        Asesoria a = asesoriaRepository.findById(asesoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asesoría no encontrada"));

        if (r.profesorId() == null || !a.getProfesor().getIdUsuario().equals(r.profesorId())) {
            throw new OperacionInvalidaException("Solo el profesor de la sesión puede registrar la atención");
        }
        if (a.getEstado() == EstadoAsesoria.FINALIZADA) {
            throw new OperacionInvalidaException("El registro de esta asesoría ya fue realizado");
        }
        if (a.getEstado() == EstadoAsesoria.CANCELADA) {
            throw new OperacionInvalidaException("No se puede registrar la atención de una asesoría cancelada");
        }
        if (a.getEstado() == EstadoAsesoria.REPROGRAMADA) {
            throw new OperacionInvalidaException("La asesoría tiene una reprogramación pendiente de respuesta");
        }

        reglasDeTiempo.exigirDiaDeLaSesion(a.getFecha(),                      
                "Solo puedes registrar la atención desde el día de la sesión");

        if (r.horaIngreso() == null || r.horaSalida() == null) {
            throw new OperacionInvalidaException("Debes indicar la hora de ingreso y la de salida");
        }
        if (!r.horaSalida().isAfter(r.horaIngreso())) {
            throw new OperacionInvalidaException("La hora de salida debe ser posterior a la de ingreso");
        }
        if (r.temaTratado() == null || r.temaTratado().isBlank()) {
            throw new OperacionInvalidaException("Debes indicar el tema tratado");
        }
        if (r.temaTratado().length() > 255
                || (r.compromisos() != null && r.compromisos().length() > 500)
                || (r.observaciones() != null && r.observaciones().length() > 500)) {
            throw new OperacionInvalidaException(
                    "El texto es demasiado largo (tema: 255 caracteres; compromisos y observaciones: 500)");
        }

        a.setHoraIngreso(r.horaIngreso());
        a.setHoraSalida(r.horaSalida());
        a.setEstado(EstadoAsesoria.FINALIZADA);

        Reporte reporte = new Reporte();
        reporte.setAsesoria(a);
        reporte.setFechaGeneracion(LocalDateTime.now());
        reporte.setTemaTratado(r.temaTratado().trim());
        reporte.setCompromisos(r.compromisos());
        reporte.setObservaciones(r.observaciones());
        reporte = reporteRepository.save(reporte);

        notificacionService.avisarEstudianteReporteDisponible(a);
        turnoService.avisarSiguienteEnEspera(a);
        return ReporteMapper.aDTO(reporte);
    }

    public ReporteDTO consultarReporte(Long asesoriaId, Long usuarioId) {
        Reporte reporte = reporteRepository.findByAsesoria_IdAsesoria(asesoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("La asesoría aún no tiene reporte"));

        Asesoria a = reporte.getAsesoria();
        boolean esElEstudiante = a.getEstudiante().getIdUsuario().equals(usuarioId);
        boolean esElProfesor = a.getProfesor().getIdUsuario().equals(usuarioId);
        if (!esElEstudiante && !esElProfesor) {
            throw new OperacionInvalidaException("No tienes acceso a este reporte");
        }
        return ReporteMapper.aDTO(reporte);
    }
}
