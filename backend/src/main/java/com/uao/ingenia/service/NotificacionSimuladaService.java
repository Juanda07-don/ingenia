package com.uao.ingenia.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.uao.ingenia.model.Asesoria;

@Service
public class NotificacionSimuladaService implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionSimuladaService.class);

    @Override
    public void avisarProfesorNuevaReserva(Asesoria a) {
        log.info("[NOTIFICACION] Para {}: {} separó un espacio de {} el {} de {} a {}",
                a.getProfesor().getCorreoInstitucional(), a.getEstudiante().getNombre(),
                a.getMateria().getNombre(), a.getFecha(), a.getHoraInicio(), a.getHoraFin());
    }

    @Override
    public void avisarEstudianteReprogramacion(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: {} propuso un nuevo horario para tu asesoría de {}",
            a.getEstudiante().getCorreoInstitucional(), a.getProfesor().getNombre(),
            a.getMateria().getNombre());
}

    @Override
    public void avisarEstudianteHorarioAgendado(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: tu nuevo horario quedó agendado para el {} de {} a {}",
            a.getEstudiante().getCorreoInstitucional(), a.getFecha(), a.getHoraInicio(), a.getHoraFin());
}

    @Override
    public void avisarProfesorCancelacion(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: {} canceló la asesoría de {}",
            a.getProfesor().getCorreoInstitucional(), a.getEstudiante().getNombre(),
            a.getMateria().getNombre());
}

    @Override
    public void avisarEstudianteReporteDisponible(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: el reporte de tu asesoría de {} ya está disponible",
            a.getEstudiante().getCorreoInstitucional(), a.getMateria().getNombre());
}

    @Override
    public void avisarEstudiantePuedePasar(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: {} ya está libre, puedes dirigirte a {}",
            a.getEstudiante().getCorreoInstitucional(), a.getProfesor().getNombre(),
            a.getProfesor().getCubiculo());
}

    @Override
    public void avisarEstudianteEspera(Asesoria a) {
    log.info("[NOTIFICACION] Para {}: {} aún no termina la asesoría anterior, espera afuera de la facultad",
            a.getEstudiante().getCorreoInstitucional(), a.getProfesor().getNombre());
}
}