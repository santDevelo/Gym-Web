package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.PlanMembresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso al catálogo de planes y validación de nombres duplicados.
public interface PlanMembresiaRepository
        extends JpaRepository<PlanMembresia, Integer> {

    List<PlanMembresia> findByActivoTrueOrderByPrecioAsc();

    boolean existsByNombreIgnoreCaseAndIdPlanNot(
            String nombre,
            Integer idPlan);
}
