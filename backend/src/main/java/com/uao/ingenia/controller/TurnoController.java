package com.uao.ingenia.controller;

import com.uao.ingenia.dto.LlegadaRequest;
import com.uao.ingenia.dto.TurnoDTO;
import com.uao.ingenia.service.TurnoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asesorias")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @PostMapping("/{id}/llegada")
    public TurnoDTO llegada(@PathVariable Long id, @RequestBody LlegadaRequest request) {
        return turnoService.registrarLlegada(id, request.estudianteId());
    }

    @GetMapping("/{id}/turno")
    public TurnoDTO turno(@PathVariable Long id, @RequestParam Long estudianteId) {
        return turnoService.consultarTurno(id, estudianteId);
    }
}
