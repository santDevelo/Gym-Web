package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Acceso a la configuración persistente de la página pública.
@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
}
