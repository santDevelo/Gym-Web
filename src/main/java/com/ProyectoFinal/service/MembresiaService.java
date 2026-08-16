package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.NombreRol;
import com.ProyectoFinal.domain.PlanMembresia;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.MembresiaRepository;
import com.ProyectoFinal.repository.PlanMembresiaRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Administra la membresía actual de cada cliente y sus reglas de vigencia.
// También proporciona los indicadores de membresías usados por el dashboard.
@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final PlanMembresiaRepository planMembresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public MembresiaService(
            MembresiaRepository membresiaRepository,
            PlanMembresiaRepository planMembresiaRepository,
            UsuarioRepository usuarioRepository) {
        this.membresiaRepository = membresiaRepository;
        this.planMembresiaRepository = planMembresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> buscarUltimaPorUsuario(Integer idUsuario) {
        return membresiaRepository
                .findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoMembresia estado) {
        return membresiaRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public BigDecimal ingresosDelMes() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return membresiaRepository.sumMontoPorEstadoEntreFechas(
                EstadoMembresia.ACTIVA, inicioMes, finMes);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarActuales() {
        return membresiaRepository.findMembresiasActuales();
    }

    @Transactional
    public Membresia guardarMembresiaCliente(
            Integer idUsuario,
            Integer idPlan,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            EstadoMembresia estado) {
        validarDatos(idUsuario, idPlan, fechaInicio, fechaVencimiento, estado);

        Usuario cliente = obtenerCliente(idUsuario);
        PlanMembresia plan = obtenerPlanActivo(idPlan);
        Membresia membresia = buscarUltimaPorUsuario(idUsuario).orElseGet(Membresia::new);

        actualizarMembresia(
                membresia, cliente, plan, fechaInicio, fechaVencimiento, estado);
        return membresiaRepository.save(membresia);
    }

    @Transactional
    public Membresia inactivarMembresiaCliente(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El cliente es obligatorio.");
        }

        Membresia membresia = buscarUltimaPorUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No tienes una membresía registrada."));
        validarEstadoCancelable(membresia.getEstado());

        membresia.setEstado(EstadoMembresia.INACTIVA);
        return membresiaRepository.save(membresia);
    }

    private Usuario obtenerCliente(Integer idUsuario) {
        Usuario cliente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe."));
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            throw new IllegalArgumentException(
                    "Solo se puede asignar una membresía a un usuario con rol CLIENTE.");
        }
        return cliente;
    }

    private PlanMembresia obtenerPlanActivo(Integer idPlan) {
        PlanMembresia plan = planMembresiaRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El plan seleccionado no existe."));
        if (!plan.isActivo()) {
            throw new IllegalArgumentException("El plan seleccionado no está activo.");
        }
        return plan;
    }

    // Copia en la entidad el plan, las fechas y el estado seleccionados por administración.
    private void actualizarMembresia(
            Membresia membresia,
            Usuario cliente,
            PlanMembresia plan,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            EstadoMembresia estado) {
        membresia.setUsuario(cliente);
        membresia.setPlanMembresia(plan);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaVencimiento);
        membresia.setEstado(estado);

        // Estas columnas heredadas siguen siendo leídas por consultas y datos existentes.
        membresia.setPlan(plan.getNombre());
        membresia.setMonto(plan.getPrecio());
        membresia.setFechaPago(fechaInicio);
    }

    private void validarEstadoCancelable(EstadoMembresia estado) {
        if (estado == EstadoMembresia.INACTIVA) {
            throw new IllegalArgumentException("La membresía ya se encuentra inactiva.");
        }
        if (estado == EstadoMembresia.VENCIDA) {
            throw new IllegalArgumentException("La membresía ya se encuentra vencida.");
        }
    }

    private void validarDatos(
            Integer idUsuario,
            Integer idPlan,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            EstadoMembresia estado) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        if (idPlan == null) {
            throw new IllegalArgumentException("Debe seleccionar un plan.");
        }
        if (fechaInicio == null) {
            throw new IllegalArgumentException("La fecha de inicio es obligatoria.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria.");
        }
        if (!fechaVencimiento.isAfter(fechaInicio)) {
            throw new IllegalArgumentException(
                    "La fecha de vencimiento debe ser posterior a la fecha de inicio.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Debe seleccionar el estado de la membresía.");
        }
    }
}
