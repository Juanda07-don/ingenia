package com.uao.ingenia.controller;

import com.uao.ingenia.dto.RegistroAtencionRequest;
import com.uao.ingenia.dto.ReporteDTO;
import com.uao.ingenia.service.AtencionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asesorias")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    @PostMapping("/{id}/atencion")
    public ResponseEntity<ReporteDTO> registrar(@PathVariable Long id,
                                                @RequestBody RegistroAtencionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atencionService.registrarAtencion(id, request));
    }

    @GetMapping("/{id}/reporte")
    public ReporteDTO reporte(@PathVariable Long id, @RequestParam Long usuarioId) {
        return atencionService.consultarReporte(id, usuarioId);
    }
}