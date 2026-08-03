package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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

    @NotBlank
    @Column(
            unique = true,
            length = 30
    )
    private String username;

    @NotBlank
    @Column(length = 512)
    private String password;

    @NotBlank
    @Column(length = 50)
    private String nombre;

    @NotBlank
    @Column(length = 80)
    private String apellidos;

    @Email
    @Column(
            unique = true,
            length = 100
    )
    private String correo;

    @Column(length = 25)
    private String telefono;

    @Column(
            name = "ruta_imagen",
            length = 1024
    )
    private String rutaImagen;

    private boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(
                    name = "id_usuario"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "id_rol"
            )
    )
    private Set<Rol> roles = new HashSet<>();

    @Transient
    public boolean tieneRol(String nombreRol) {

        return roles.stream().anyMatch(
                rol -> rol.getRol()
                        .equalsIgnoreCase(nombreRol)
        );
    }
}