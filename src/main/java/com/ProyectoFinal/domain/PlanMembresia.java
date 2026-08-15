package com.ProyectoFinal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

// Entidad JPA para la tabla "plan_membresia": catálogo de planes que ofrece
// el gimnasio (Plan Básico, Plan Fit, Plan Completo). Un plan inactivo no se
// puede asignar a un cliente nuevo, pero se conserva para el historial.
@Data
@Entity
@Table(name = "plan_membresia")
public class PlanMembresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    // BigDecimal para dinero: evita los errores de redondeo de double/float
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean activo;
}