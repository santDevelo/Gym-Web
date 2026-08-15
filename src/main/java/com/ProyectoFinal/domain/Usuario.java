package com.ProyectoFinal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    @Column(unique = true, length = 30)
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
    @Column(unique = true, length = 100)
    private String correo;

    @Column(length = 25)
    private String telefono;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    private boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();

    @Transient
    public boolean tieneRol(String nombreRol) {
        return nombreRol != null
                && roles.stream().anyMatch(rol -> nombreRol.equalsIgnoreCase(rol.getRol()));
    }

    @Transient
    public boolean tieneRol(NombreRol nombreRol) {
        return nombreRol != null && tieneRol(nombreRol.name());
    }
}
