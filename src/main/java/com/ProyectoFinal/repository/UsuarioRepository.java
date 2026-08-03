package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(
            String username
    );

    Optional<Usuario> findByUsernameAndActivoTrue(
            String username
    );

    Optional<Usuario> findByUsernameAndPassword(
            String username,
            String password
    );

    Optional<Usuario> findByUsernameOrCorreo(
            String username,
            String correo
    );

    boolean existsByUsernameOrCorreo(
            String username,
            String correo
    );

    List<Usuario> findByActivoTrue();

    @Query("""
           SELECT COUNT(DISTINCT u)
           FROM Usuario u
           JOIN u.roles r
           WHERE r.rol = :rol
             AND u.activo = true
           """)
    long contarActivosPorRol(
            @Param("rol") String rol
    );

    @Query("""
           SELECT DISTINCT u
           FROM Usuario u
           JOIN u.roles r
           WHERE r.rol = :rol
           """)
    List<Usuario> listarPorNombreRol(
            @Param("rol") String rol
    );
}