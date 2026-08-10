package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(
            String username);

    Optional<Usuario> findByUsernameAndActivoTrue(
            String username);

    Optional<Usuario> findByCorreo(
            String correo);

    Optional<Usuario> findByUsernameOrCorreo(
            String username,
            String correo);

    boolean existsByUsernameOrCorreo(
            String username,
            String correo);

    List<Usuario> findByActivoTrue();

    long countByRoles_RolAndActivoTrue(
            String rol);
}