package com.fitsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre no puede estar vacio.")
    @Size(max = 50, message = "El nombre no puede tener mas de 50 caracteres.")
    private String nombre;

    @Column(nullable = false, length = 80)
    @NotBlank(message = "Los apellidos no pueden estar vacios.")
    @Size(max = 80, message = "Los apellidos no pueden tener mas de 80 caracteres.")
    private String apellidos;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "El correo no puede estar vacio.")
    @Email(message = "El correo no tiene un formato valido.")
    @Size(max = 100, message = "El correo no puede tener mas de 100 caracteres.")
    private String correo;

    @Column(unique = true, nullable = false, length = 30)
    @NotBlank(message = "El usuario no puede estar vacio.")
    @Size(max = 30, message = "El usuario no puede tener mas de 30 caracteres.")
    private String username;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "La contrasena no puede estar vacia.")
    @Size(max = 100, message = "La contrasena no puede tener mas de 100 caracteres.")
    private String password;

    @Column(length = 20)
    @Size(max = 20, message = "El telefono no puede tener mas de 20 caracteres.")
    private String telefono;

    @Column(nullable = false, length = 20)
    @NotNull(message = "El rol no puede estar vacio.")
    @Enumerated(EnumType.STRING)
    private RolUsuario rol;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "ruta_imagen", length = 1024)
    @Size(max = 1024)
    private String rutaImagen;
}
