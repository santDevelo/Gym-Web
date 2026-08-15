package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.PlanMembresia;
import com.ProyectoFinal.repository.PlanMembresiaRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanMembresiaService {

    private final PlanMembresiaRepository planMembresiaRepository;

    public PlanMembresiaService(PlanMembresiaRepository planMembresiaRepository) {
        this.planMembresiaRepository = planMembresiaRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanMembresia> listarActivos() {
        return planMembresiaRepository.findByActivoTrueOrderByPrecioAsc();
    }

    @Transactional(readOnly = true)
    public List<PlanMembresia> listarTodos() {
        return planMembresiaRepository.findAll();
    }

    @Transactional
    public PlanMembresia actualizar(
            Integer idPlan,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean activo) {
        validarDatos(idPlan, nombre, precio);

        String nombreLimpio = nombre.trim();
        validarNombreDisponible(nombreLimpio, idPlan);

        PlanMembresia plan = planMembresiaRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException("El plan no existe."));
        plan.setNombre(nombreLimpio);
        plan.setDescripcion(limpiarTextoOpcional(descripcion));
        plan.setPrecio(precio);
        plan.setActivo(activo);
        return planMembresiaRepository.save(plan);
    }

    private void validarDatos(Integer idPlan, String nombre, BigDecimal precio) {
        if (idPlan == null) {
            throw new IllegalArgumentException("El plan no fue seleccionado.");
        }
        if (!tieneTexto(nombre)) {
            throw new IllegalArgumentException("El nombre del plan es obligatorio.");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
    }

    private void validarNombreDisponible(String nombre, Integer idPlan) {
        if (planMembresiaRepository.existsByNombreIgnoreCaseAndIdPlanNot(nombre, idPlan)) {
            throw new IllegalArgumentException("Ya existe otro plan con ese nombre.");
        }
    }

    private String limpiarTextoOpcional(String texto) {
        return tieneTexto(texto) ? texto.trim() : null;
    }

    private boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
