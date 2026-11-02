package com.uao.ingenia.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.uao.ingenia.dto.ProfesorDTO;

import com.uao.ingenia.dto.DisponibilidadDTO;
import com.uao.ingenia.dto.MateriaDTO;
import com.uao.ingenia.service.BusquedaService;

@RestController
@RequestMapping("/api")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping("/profesores")
    public List<ProfesorDTO> buscar(@RequestParam(required = false) String materia,
                                    @RequestParam(required = false) String nombre) {
        if (materia != null) return busquedaService.buscarPorMateria(materia);
        if (nombre != null) return busquedaService.buscarPorNombre(nombre);
        return List.of();
    }

    @GetMapping("/profesores/{id}/disponibilidad")
    public List<DisponibilidadDTO> disponibilidad(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return busquedaService.disponibilidad(id, fecha);
    }

    @GetMapping("/profesores/{id}/materias")
    public List<MateriaDTO> materias(@PathVariable Long id) {
    return busquedaService.materiasDelProfesor(id);
}
}
