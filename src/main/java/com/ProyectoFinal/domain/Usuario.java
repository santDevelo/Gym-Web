package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    private String nombre;

    private String apellidos;

    private String correo;

    private String username;

    private String password;

    private String telefono;

    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    private Boolean activo;

    @Column(name = "ruta_imagen")
    private String rutaImagen;
}