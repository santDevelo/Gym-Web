package com.ProyectoFinal.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

// Entidad JPA: se mapea a la tabla "usuario" (patrón Entidad -> Repository ->
// Service -> Controller visto en clase). Lombok (@Data) genera automáticamente
// getters, setters, equals/hashCode y toString.
@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    // Clave primaria autoincremental (IDENTITY delega el autoincremento a MySQL)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    // @NotBlank activa la validación de spring-boot-starter-validation
    @NotBlank
    @Column(
            unique = true,
            length = 30
    )
    private String username;

    // Se guarda cifrada con BCrypt (PasswordEncoder), nunca en texto plano
    @NotBlank
    @Column(length = 512)
    private String password;

    @NotBlank
    @Column(length = 50)
    private String nombre;

    @NotBlank
    @Column(length = 80)
    private String apellidos;

    // @Email valida el formato de correo antes de llegar al service
    @Email
    @Column(
            unique = true,
            length = 100
    )
    private String correo;

    @Column(length = 25)
    private String telefono;

    // URL de la foto de perfil subida a Firebase Storage
    @Column(
            name = "ruta_imagen",
            length = 1024
    )
    private String rutaImagen;

    // Activo/inactivo: se usa para "borrado lógico" en vez de eliminar el usuario
    private boolean activo;

    // Relación muchos a muchos con Rol, a través de la tabla intermedia usuario_rol
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

    // Método de conveniencia (no es columna de BD, @Transient) para preguntar
    // si el usuario tiene un rol dado, usado en controladores y en Thymeleaf
    @Transient
    public boolean tieneRol(String nombreRol) {

        return roles.stream().anyMatch(
                rol -> rol.getRol()
                        .equalsIgnoreCase(nombreRol)
        );
    }
}