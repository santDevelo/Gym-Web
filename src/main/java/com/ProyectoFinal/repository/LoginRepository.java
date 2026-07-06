package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Login;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Integer> {

    Optional<Login> findByUsernameAndPasswordAndRol(
            String username,
            String password,
            Rol rol);

    Optional<Login> findByUsername(String username);

}