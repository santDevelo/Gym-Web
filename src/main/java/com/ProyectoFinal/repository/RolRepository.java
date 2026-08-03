package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository
        extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByRol(String rol);
}