package com.uao.ingenia.service;

import com.uao.ingenia.dto.TurnoDTO;
import com.uao.ingenia.exception.OperacionInvalidaException;
import com.uao.ingenia.exception.RecursoNoEncontradoException;
import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.EstadoAsesoria;
import com.uao.ingenia.repository.AsesoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TurnoService {

    private final AsesoriaRepository asesoriaRepository;
    private final NotificacionService notificacionService;
    private final ReglasDeTiempo reglasDeTiempo;

    public TurnoService(AsesoriaRepository asesoriaRepository,
                        NotificacionService notificacionService,
                        ReglasDeTiempo reglasDeTiempo) {
        this.asesoriaRepository = asesoriaRepository;
        this.notificacionService = notificacionService;
        this.reglasDeTiempo = reglasDeTiempo;
    }

    @Transactional
    public TurnoDTO registrarLlegada(Long asesoriaId, Long estudianteId) {
        Asesoria a = validar(asesoriaId, estudianteId);
        if (a.getHoraLlegada() == null) {
            a.setHoraLlegada(LocalDateTime.now());
        }
        TurnoDTO turno = evaluar(a);
        if (turno.puedePasar()) {
            notificacionService.avisarEstudiantePuedePasar(a);
        } else {
            notificacionService.avisarEstudianteEspera(a);
        }
        return turno;
    }

    public TurnoDTO consultarTurno(Long asesoriaId, Long estudianteId) {
        return evaluar(validar(asesoriaId, estudianteId));
    }

    // Lo llama AtencionService cuando el profesor termina de registrar una atención
    @Transactional
    public void avisarSiguienteEnEspera(Asesoria finalizada) {
        asesoriaRepository
                .findFirstByProfesor_IdUsuarioAndFechaAndEstadoOrderByHoraInicioAsc(
                        finalizada.getProfesor().getIdUsuario(), finalizada.getFecha(),
                        EstadoAsesoria.AGENDADA)
                .filter(siguiente -> siguiente.getHoraLlegada() != null)
                .ifPresent(notificacionService::avisarEstudiantePuedePasar);
    }

    private Asesoria validar(Long asesoriaId, Long estudianteId) {
        Asesoria a = asesoriaRepository.findById(asesoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asesoría no encontrada"));
        if (!a.getEstudiante().getIdUsuario().equals(estudianteId)) {
            throw new OperacionInvalidaException("Esta asesoría no te pertenece");
        }
        if (a.getEstado() != EstadoAsesoria.AGENDADA) {
            throw new OperacionInvalidaException("La asesoría no está agendada");
        }
        reglasDeTiempo.exigirDiaDeLaSesion(a.getFecha(),                        
                "Solo puedes avisar tu llegada el día de la asesoría");
        return a;
    }

    private TurnoDTO evaluar(Asesoria a) {
        boolean profesorOcupado = asesoriaRepository
                .existsByProfesor_IdUsuarioAndFechaAndEstadoAndHoraInicioLessThan(
                        a.getProfesor().getIdUsuario(), a.getFecha(),
                        EstadoAsesoria.AGENDADA, a.getHoraInicio());
        if (profesorOcupado) {
            return new TurnoDTO(false,
                    "El profesor aún no termina la asesoría anterior. Espera afuera de la facultad; te avisaremos cuando puedas pasar.",
                    null);
        }
        return new TurnoDTO(true, "Puedes dirigirte al cubículo del profesor",
                a.getProfesor().getCubiculo());
    }
}
