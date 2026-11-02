package com.uao.ingenia.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.EnumType;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(name = "asesoria")
public class Asesoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asesoria")
    private Long idAsesoria;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "materia_id")
    private Materia materia;

    @ManyToOne(optional = false)
    @JoinColumn(name = "disponibilidad_id")
    private Disponibilidad disponibilidad;

    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "hora_ingreso")
    private LocalTime horaIngreso;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

    @Enumerated(EnumType.STRING)
    private EstadoAsesoria estado = EstadoAsesoria.AGENDADA;

    @ManyToOne
    @JoinColumn(name = "disponibilidad_propuesta_id")
    private Disponibilidad disponibilidadPropuesta;

    @Column(name = "motivo_reprogramacion")
    private String motivoReprogramacion;

    @Column(name = "hora_llegada")
    private LocalDateTime horaLlegada;
}
