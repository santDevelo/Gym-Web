package com.fitsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "cliente_membresia")
public class ClienteMembresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente_membresia")
    private Integer idClienteMembresia;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    @NotNull(message = "El cliente no puede estar vacio.")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_membresia")
    @NotNull(message = "El plan de membresia no puede estar vacio.")
    private Membresia membresia;

    @Column(name = "fecha_inicio", nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacia.")
    private LocalDate fechaInicio;

    // No lleva @NotNull: se calcula en ClienteMembresiaService a partir de fechaInicio + duracionDias del plan,
    // igual que rutaImagen en Producto/Categoria se completa despues en el service (no lo llena el formulario).
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;
}
