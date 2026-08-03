package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "ruta")
public class Ruta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @Column(
            name = "ruta",
            length = 255,
            nullable = false
    )
    private String ruta;

    @Column(name = "requiere_rol")
    private boolean requiereRol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;
}