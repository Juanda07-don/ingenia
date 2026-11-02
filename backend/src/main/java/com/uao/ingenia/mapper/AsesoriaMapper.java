package com.uao.ingenia.mapper;

import com.uao.ingenia.dto.AsesoriaDTO;
import com.uao.ingenia.dto.PropuestaDTO;
import com.uao.ingenia.model.Asesoria;
import com.uao.ingenia.model.Disponibilidad;

public final class AsesoriaMapper {

    private AsesoriaMapper() {}

    public static AsesoriaDTO aDTO(Asesoria a) {
        Disponibilidad p = a.getDisponibilidadPropuesta();
        PropuestaDTO propuesta = (p == null) ? null
                : new PropuestaDTO(p.getFecha(), p.getHoraInicio(), p.getHoraFin(),
                        a.getProfesor().getCubiculo(), a.getMotivoReprogramacion());
        return new AsesoriaDTO(a.getIdAsesoria(), "ING-" + a.getIdAsesoria(),
                a.getEstado().name(), a.getMateria().getNombre(), a.getProfesor().getNombre(),
                a.getEstudiante().getNombre(), a.getFecha(), a.getHoraInicio(), a.getHoraFin(),
                a.getProfesor().getCubiculo(), propuesta);
    }
}
