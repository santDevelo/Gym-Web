package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Acceso a usuarios, autenticación y filtros por rol.
@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(
            String username);

    Optional<Usuario> findByUsernameAndActivoTrue(
            String username);

    Optional<Usuario> findByCorreo(
            String correo);

    List<Usuario> findDistinctByRoles_Rol(
            String rol);

    List<Usuario> findDistinctByRoles_RolAndActivoTrue(
            String rol);

    long countByRoles_RolAndActivoTrue(
            String rol);
}
