package com.example.myapplication;

import java.util.List;

public class ProfesorDTO {
    private Long id;
    private String nombre;
    private List<String> materias;
    private String cubiculo;
    private ProximoEspacioDTO proximoEspacio;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public List<String> getMaterias() { return materias; }
    public String getCubiculo() { return cubiculo; }
    public ProximoEspacioDTO getProximoEspacio() { return proximoEspacio; }
}
