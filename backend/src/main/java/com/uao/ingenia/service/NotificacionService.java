package com.uao.ingenia.service;

import com.uao.ingenia.model.Asesoria;

public interface NotificacionService {
    void avisarProfesorNuevaReserva(Asesoria asesoria);
    void avisarEstudianteReprogramacion(Asesoria asesoria);
    void avisarEstudianteHorarioAgendado(Asesoria asesoria);
    void avisarProfesorCancelacion(Asesoria asesoria);
    void avisarEstudianteReporteDisponible(Asesoria asesoria);
    void avisarEstudiantePuedePasar(Asesoria asesoria);
    void avisarEstudianteEspera(Asesoria asesoria);

}