package com.fitsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "membresia")
public class Membresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer idMembresia;

    @Column(name = "nombre_plan", unique = true, nullable = false, length = 50)
    @NotBlank(message = "El nombre del plan no puede estar vacio.")
    @Size(max = 50, message = "El nombre del plan no puede tener mas de 50 caracteres.")
    private String nombrePlan;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 12, scale = 2, nullable = false)
    @NotNull(message = "El precio no puede estar vacio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El precio debe ser mayor a 0.")
    private BigDecimal precio;

    @Column(name = "duracion_dias", nullable = false)
    @NotNull(message = "La duracion no puede estar vacia.")
    @Min(value = 1, message = "La duracion debe ser al menos 1 dia.")
    private Integer duracionDias;

    @Column(name = "activo")
    private Boolean activo;
}
