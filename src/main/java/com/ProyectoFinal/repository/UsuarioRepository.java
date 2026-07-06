package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.domain.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsernameAndPasswordAndRol(
            String username,
            String password,
            RolUsuario rol);

    Optional<Usuario> findByUsername(String username);

}