package com.uao.ingenia.model;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ESTUDIANTE")
public class Estudiante extends Usuario {
}
