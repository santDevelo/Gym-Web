package com.ProyectoFinal.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

// Entidad JPA para la tabla "rutina": la rutina de ejercicios activa de un
// cliente, asignada por un entrenador. Solo puede haber una rutina activa por
// cliente a la vez (esa regla se valida en RutinaService, no aquí).
//
// Se usa @Getter/@Setter en vez de @Data a propósito: como esta clase tiene
// una lista de EjercicioRutina y EjercicioRutina apunta de vuelta a Rutina
// (relación bidireccional), un equals/hashCode/toString generado por @Data
// en ambos lados terminaría llamándose uno a otro sin parar (recursión
// infinita / StackOverflowError). @Getter/@Setter evita ese problema.
@Getter
@Setter
@Entity
@Table(name = "rutina")
public class Rutina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Integer idRutina;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Usuario cliente;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 255)
    private String objetivo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(nullable = false)
    private boolean activa;

    // Relación uno a muchos con sus ejercicios. cascade = ALL + orphanRemoval
    // hace que, al borrar la rutina o quitar un ejercicio de la lista, JPA
    // borre también las filas de ejercicio_rutina sin tener que hacerlo a mano.
    @OneToMany(
            mappedBy = "rutina",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<EjercicioRutina> ejercicios
            = new ArrayList<>();
}
