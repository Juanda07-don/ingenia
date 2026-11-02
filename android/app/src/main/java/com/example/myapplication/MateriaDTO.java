package com.example.myapplication;

public class MateriaDTO {
    private Long id;
    private String nombre;

    public Long getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
