package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

// Entidad JPA para la tabla "rol" (catálogo simple: ADMINISTRADOR, ENTRENADOR,
// CLIENTE). Se relaciona con Usuario mediante la tabla intermedia usuario_rol.
@Data
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(
            name = "rol",
            unique = true,
            length = 20
    )
    private String rol;
}