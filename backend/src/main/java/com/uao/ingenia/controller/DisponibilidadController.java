package com.uao.ingenia.controller;

import com.uao.ingenia.dto.ConfigurarEspaciosRequest;
import com.uao.ingenia.dto.EspacioDTO;
import com.uao.ingenia.service.DisponibilidadService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/profesores/{profesorId}/espacios")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @GetMapping
    public List<EspacioDTO> listar(@PathVariable Long profesorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return disponibilidadService.listarEspacios(profesorId, fecha);
    }

    @PostMapping
    public ResponseEntity<List<EspacioDTO>> crear(@PathVariable Long profesorId,
                                                @RequestBody ConfigurarEspaciosRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disponibilidadService.crearEspacios(profesorId, request));
    }

    @DeleteMapping("/{espacioId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long profesorId, @PathVariable Long espacioId) {
        disponibilidadService.eliminarEspacio(profesorId, espacioId);
        return ResponseEntity.noContent().build();
    }
}
