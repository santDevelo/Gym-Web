package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.PlanMembresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanMembresiaRepository
        extends JpaRepository<PlanMembresia, Integer> {

    List<PlanMembresia> findByActivoTrueOrderByPrecioAsc();

    boolean existsByNombreIgnoreCaseAndIdPlanNot(
            String nombre,
            Integer idPlan);
}
