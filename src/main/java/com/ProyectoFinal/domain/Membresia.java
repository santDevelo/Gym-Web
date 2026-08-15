package com.ProyectoFinal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

// Entidad JPA para la tabla "membresia": relaciona a un cliente con su plan
// vigente (fechas y estado). Cada vez que se asigna o renueva una membresía se
// actualiza esta misma fila; el historial de pagos vive aparte, en Pago.
@Data
@Entity
@Table(name = "membresia")
public class Membresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer idMembresia;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /*
     * Nueva relación con el catálogo de planes.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_plan")
    private PlanMembresia planMembresia;

    /*
     * Campos anteriores. Se conservan temporalmente porque el dashboard
     * y las consultas actuales todavía los utilizan.
     */
    private String plan;

    private BigDecimal monto;

    @Column(name = "fecha_pago")
    private LocalDate fechaPago;

    /*
     * Nuevas fechas para controlar la vigencia de la membresía.
     */
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private EstadoMembresia estado;
}