package com.uao.ingenia.mapper;

import com.uao.ingenia.dto.ReporteDTO;
import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.Reporte;

public final class ReporteMapper {

    private ReporteMapper() {}

    public static ReporteDTO aDTO(Reporte r) {
        Asesoria a = r.getAsesoria();
        return new ReporteDTO(a.getIdAsesoria(), "ING-" + a.getIdAsesoria(),
                a.getMateria().getNombre(), a.getProfesor().getNombre(), a.getEstudiante().getNombre(),
                a.getFecha(), a.getHoraIngreso(), a.getHoraSalida(), r.getTemaTratado(),
                r.getObservaciones(), r.getCompromisos(), r.getFechaGeneracion());
    }
}
