package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.PlanMembresia;
import com.ProyectoFinal.repository.PlanMembresiaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Capa de servicio: reglas de negocio del catálogo de planes de membresía.
@Service
public class PlanMembresiaService {

    private final PlanMembresiaRepository planMembresiaRepository;

    public PlanMembresiaService(
            PlanMembresiaRepository planMembresiaRepository) {
        this.planMembresiaRepository = planMembresiaRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanMembresia> listarActivos() {
        return planMembresiaRepository
                .findByActivoTrueOrderByPrecioAsc();
    }

    @Transactional(readOnly = true)
    public List<PlanMembresia> listarTodos() {
        return planMembresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<PlanMembresia> buscarPorId(Integer idPlan) {
        return planMembresiaRepository.findById(idPlan);
    }

    // Edita un plan existente (no se crean planes nuevos desde la pantalla
    // de administración, solo se ajustan precio/descripción/activo).
    @Transactional
    public PlanMembresia actualizar(
            Integer idPlan,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean activo) {

        if (idPlan == null) {
            throw new IllegalArgumentException(
                    "El plan no fue seleccionado.");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del plan es obligatorio.");
        }

        if (precio == null
                || precio.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero.");
        }

        String nombreLimpio = nombre.trim();

        if (planMembresiaRepository
                .existsByNombreIgnoreCaseAndIdPlanNot(
                        nombreLimpio,
                        idPlan)) {

            throw new IllegalArgumentException(
                    "Ya existe otro plan con ese nombre.");
        }

        PlanMembresia plan = planMembresiaRepository
                .findById(idPlan)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El plan no existe."));

        plan.setNombre(nombreLimpio);
        plan.setDescripcion(
                descripcion == null || descripcion.isBlank()
                        ? null
                        : descripcion.trim());
        plan.setPrecio(precio);
        plan.setActivo(activo);

        return planMembresiaRepository.save(plan);
    }
}
