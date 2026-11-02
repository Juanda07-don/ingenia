package com.uao.ingenia.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.JoinColumn;

@Getter
@Setter
@NoArgsConstructor

@Entity
@DiscriminatorValue("PROFESOR")
public class Profesor extends Usuario {
    
    private String cubiculo;

    @ManyToMany
    @JoinTable(name = "profesor_materia",
        joinColumns = @JoinColumn(name = "profesor_id"),
        inverseJoinColumns = @JoinColumn(name = "materia_id"))
    private Set<Materia> materias = new HashSet<>();
}