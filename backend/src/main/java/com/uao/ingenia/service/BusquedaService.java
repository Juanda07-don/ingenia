package com.uao.ingenia.service;

import com.uao.ingenia.dto.*;
import com.uao.ingenia.exception.RecursoNoEncontradoException;
import com.uao.ingenia.model.*;
import com.uao.ingenia.repository.DisponibilidadRepository;
import com.uao.ingenia.repository.ProfesorRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class BusquedaService {

private final ProfesorRepository profesorRepository;
private final DisponibilidadRepository disponibilidadRepository;
private final ReglasDeTiempo reglasDeTiempo;

public BusquedaService(ProfesorRepository profesorRepository,
                        DisponibilidadRepository disponibilidadRepository,
                        ReglasDeTiempo reglasDeTiempo) {
        this.profesorRepository = profesorRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.reglasDeTiempo = reglasDeTiempo;
}

public List<ProfesorDTO> buscarPorMateria(String materia) {
        return profesorRepository
                .findDistinctByMaterias_NombreContainingIgnoreCase(materia)
                .stream().map(this::aDTO).toList();
}

public List<ProfesorDTO> buscarPorNombre(String nombre) {
        return profesorRepository
                .findByNombreContainingIgnoreCase(nombre)
                .stream().map(this::aDTO).toList();
}

public List<DisponibilidadDTO> disponibilidad(Long profesorId, LocalDate fecha) {
        return disponibilidadRepository
                .findByProfesor_IdUsuarioAndFechaOrderByHoraInicio(profesorId, fecha)
                .stream()
                .map(d -> new DisponibilidadDTO(d.getIdDisponibilidad(), d.getFecha(),
                        d.getHoraInicio(), d.getHoraFin(), d.getProfesor().getCubiculo(), d.getEstado().name(),
                        d.getEstado() == EstadoDisponibilidad.LIBRE
                                && !reglasDeTiempo.yaPaso(d.getFecha(), d.getHoraInicio())))
                .toList();
}

public List<MateriaDTO> materiasDelProfesor(Long profesorId) {
        Profesor p = profesorRepository.findById(profesorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado"));
        return p.getMaterias().stream()
                .map(m -> new MateriaDTO(m.getIdMateria(), m.getNombre())).toList();
}

private ProfesorDTO aDTO(Profesor p) {
        ProximoEspacioDTO proximo = disponibilidadRepository
                .proximosLibres(p.getIdUsuario(), EstadoDisponibilidad.LIBRE,
                        LocalDate.now(), LocalTime.now(), PageRequest.of(0, 1))
                .stream().findFirst()
                .map(d -> new ProximoEspacioDTO(d.getFecha(), d.getHoraInicio()))
                .orElse(null);
        return new ProfesorDTO(p.getIdUsuario(), p.getNombre(),
                p.getMaterias().stream().map(Materia::getNombre).toList(),
                p.getCubiculo(), proximo);
}
}
