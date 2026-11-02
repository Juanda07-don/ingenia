package com.uao.ingenia.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uao.ingenia.dto.AsesoriaDTO;
import com.uao.ingenia.dto.ReprogramarRequest;
import com.uao.ingenia.dto.RespuestaReprogramacionRequest;
import com.uao.ingenia.service.ReprogramacionService;

@RestController
@RequestMapping("/api/asesorias")
public class ReprogramacionController {

    private final ReprogramacionService reprogramacionService;

    public ReprogramacionController(ReprogramacionService reprogramacionService) {
        this.reprogramacionService = reprogramacionService;
    }

    @PostMapping("/{id}/reprogramacion")
    public AsesoriaDTO reprogramar(@PathVariable Long id, @RequestBody ReprogramarRequest r) {
        return reprogramacionService.reprogramar(id, r.profesorId(), r.nuevaDisponibilidadId(), r.motivo());
    }

    @PostMapping("/{id}/respuesta-reprogramacion")
    public AsesoriaDTO responder(@PathVariable Long id, @RequestBody RespuestaReprogramacionRequest r) {
        return reprogramacionService.responder(id, r.estudianteId(), r.aceptar());
    }
}
