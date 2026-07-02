package com.fitsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity
@Table(name = "pago")
public class Pago implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @ManyToOne
    @JoinColumn(name = "id_cliente_membresia")
    @NotNull(message = "La membresia asociada no puede estar vacia.")
    private ClienteMembresia clienteMembresia;

    @Column(precision = 12, scale = 2, nullable = false)
    @NotNull(message = "El monto no puede estar vacio.")
    @DecimalMin(value = "0.01", inclusive = true, message = "El monto debe ser mayor a 0.")
    private BigDecimal monto;

    @Column(name = "fecha_pago", nullable = false)
    @NotNull(message = "La fecha de pago no puede estar vacia.")
    private LocalDate fechaPago;

    @Column(name = "metodo_pago", nullable = false, length = 20)
    @NotNull(message = "El metodo de pago no puede estar vacio.")
    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago;
}
