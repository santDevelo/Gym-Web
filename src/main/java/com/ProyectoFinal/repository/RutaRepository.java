package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Ruta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Acceso a las reglas de autorización configuradas por ruta.
@Repository
public interface RutaRepository
        extends JpaRepository<Ruta, Integer> {

    List<Ruta> findAllByOrderByRequiereRolAsc();
}
