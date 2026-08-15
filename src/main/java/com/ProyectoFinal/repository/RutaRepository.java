package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository (capa DAO) para Ruta.
@Repository
public interface RutaRepository
        extends JpaRepository<Ruta, Integer> {

    // Trae primero las rutas públicas (requiereRol = false) para que
    // SecurityConfig las registre antes que las protegidas
    List<Ruta> findAllByOrderByRequiereRolAsc();
}