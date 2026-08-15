package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.PlanMembresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository (capa DAO) para PlanMembresia.
public interface PlanMembresiaRepository
        extends JpaRepository<PlanMembresia, Integer> {

    // Planes que un cliente puede contratar, ordenados de más barato a más caro
    List<PlanMembresia> findByActivoTrueOrderByPrecioAsc();

    // Verifica nombre duplicado al editar, ignorando el propio plan (IdPlanNot)
    boolean existsByNombreIgnoreCaseAndIdPlanNot(
            String nombre,
            Integer idPlan);
}
