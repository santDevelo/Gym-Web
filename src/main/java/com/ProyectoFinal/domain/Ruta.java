package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

// Entidad JPA para la tabla "ruta": guarda en BD qué URL requiere qué rol.
// SecurityConfig lee esta tabla al arrancar para armar el securityFilterChain
// dinámicamente, en vez de tener las URLs escritas a mano en el código.
@Data
@Entity
@Table(name = "ruta")
public class Ruta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    // Patrón de URL, por ejemplo "/admin/**"
    @Column(
            name = "ruta",
            length = 255,
            nullable = false
    )
    private String ruta;

    // Si es false, la ruta es pública (permitAll); si es true, exige el rol de "rol"
    @Column(name = "requiere_rol")
    private boolean requiereRol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;
}