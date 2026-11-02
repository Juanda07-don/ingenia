package com.uao.ingenia.dto;

import java.util.List;

public record ProfesorDTO(Long id, String nombre, List<String> materias,
                        String cubiculo, ProximoEspacioDTO proximoEspacio) {}