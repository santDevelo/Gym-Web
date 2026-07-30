package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Login;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Integer> {

    Optional<Login> findByUsernameAndPasswordAndRol(
            String username,
            String password,
            Rol rol);

    Optional<Login> findByUsername(String username);

    Optional<Login> findByUsernameAndPassword(String username, String password);

    long countByRolAndActivoTrue(Rol rol);

    List<Login> findByRol(Rol rol);

}
