package com.fitsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "rutina")
public class Rutina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Integer idRutina;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "El nombre de la rutina no puede estar vacio.")
    @Size(max = 80, message = "El nombre no puede tener mas de 80 caracteres.")
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_entrenador")
    @NotNull(message = "El entrenador no puede estar vacio.")
    private Usuario entrenador;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    @NotNull(message = "El cliente no puede estar vacio.")
    private Usuario cliente;

    @Column(name = "fecha_asignacion", nullable = false)
    @NotNull(message = "La fecha de asignacion no puede estar vacia.")
    private LocalDate fechaAsignacion;

    @Column(name = "activo")
    private Boolean activo;
}
