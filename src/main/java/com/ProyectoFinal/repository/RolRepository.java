package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository (capa DAO) para Rol.
@Repository
public interface RolRepository
        extends JpaRepository<Rol, Integer> {

    // Busca el rol por su nombre (ej. "CLIENTE"), usado al asignar rol por defecto
    Optional<Rol> findByRol(String rol);
}