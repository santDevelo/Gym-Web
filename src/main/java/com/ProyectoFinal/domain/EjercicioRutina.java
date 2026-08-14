package com.ProyectoFinal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ejercicio_rutina")
public class EjercicioRutina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejercicio")
    private Integer idEjercicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @Column(nullable = false, length = 20)
    private String dia;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Integer series;

    @Column(nullable = false, length = 30)
    private String repeticiones;

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    private Integer orden;
}
