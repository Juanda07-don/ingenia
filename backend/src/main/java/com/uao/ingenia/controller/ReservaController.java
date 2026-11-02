package com.uao.ingenia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uao.ingenia.dto.AsesoriaDTO;
import com.uao.ingenia.dto.ReservaRequest;
import com.uao.ingenia.service.ReservaService;

@RestController
@RequestMapping("/api")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping("/asesorias")
    public ResponseEntity<AsesoriaDTO> reservar(@RequestBody ReservaRequest request) {
        AsesoriaDTO creada = reservaService.reservar(request.estudianteId(), request.disponibilidadId());
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/estudiantes/{id}/asesorias")
    public List<AsesoriaDTO> misAsesorias(@PathVariable Long id) {
        return reservaService.asesoriasDelEstudiante(id);
    }

    @GetMapping("/profesores/{id}/asesorias")
    public List<AsesoriaDTO> agendaProfesor(@PathVariable Long id) {
    return reservaService.asesoriasDelProfesor(id);
}
}
