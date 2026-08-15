package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository (capa DAO) para Usuario. Spring Data JPA genera la consulta a
// partir del nombre del método, sin escribir SQL a mano (consultas derivadas,
// ver semana8 del curso). "Roles_Rol" navega la relación usuario -> roles ->
// rol.rol para poder filtrar por nombre de rol.
@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(
            String username);

    // Usado en el login: solo deja entrar si el usuario está activo
    Optional<Usuario> findByUsernameAndActivoTrue(
            String username);

    Optional<Usuario> findByCorreo(
            String correo);

    Optional<Usuario> findByUsernameOrCorreo(
            String username,
            String correo);

    // Verifica duplicados antes de crear un usuario (registro y alta por admin)
    boolean existsByUsernameOrCorreo(
            String username,
            String correo);

    List<Usuario> findByActivoTrue();

    // Lista usuarios que tengan un rol determinado (por ejemplo "CLIENTE")
    List<Usuario> findDistinctByRoles_Rol(
            String rol);

    List<Usuario> findDistinctByRoles_RolAndActivoTrue(
            String rol);

    // Cuenta para las tarjetas de resumen del dashboard (clientes/entrenadores activos)
    long countByRoles_RolAndActivoTrue(
            String rol);
}
