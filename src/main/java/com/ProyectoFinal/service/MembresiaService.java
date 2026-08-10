package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
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
    public Optional<Membresia> buscarUltimaPorUsuario(
            Integer idUsuario) {

        return membresiaRepository
                .findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(
                        idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoMembresia estado) {
        return membresiaRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public BigDecimal ingresosDelMes() {

        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.withDayOfMonth(1);
        LocalDate fin = hoy.withDayOfMonth(
                hoy.lengthOfMonth());

        return membresiaRepository
                .sumMontoPorEstadoEntreFechas(
                        EstadoMembresia.ACTIVA,
                        inicio,
                        fin);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarActuales() {
        return membresiaRepository.findMembresiasActuales();
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarTodas() {
        return membresiaRepository.findAll();
    }

    /*
     * Asigna una membresía nueva o modifica la membresía
     * actual de un cliente.
     */
    @Transactional
    public Membresia guardarMembresiaCliente(
            Integer idUsuario,
            Integer idPlan,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            EstadoMembresia estado) {

        validarDatos(
                idUsuario,
                idPlan,
                fechaInicio,
                fechaVencimiento,
                estado);

        Usuario cliente = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El cliente no existe."));

        if (!cliente.tieneRol("CLIENTE")) {

            throw new IllegalArgumentException(
                    "Solo se puede asignar una membresía "
                    + "a un usuario con rol CLIENTE.");
        }

        PlanMembresia planSeleccionado
                = planMembresiaRepository
                        .findById(idPlan)
                        .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El plan seleccionado no existe."));

        if (!planSeleccionado.isActivo()) {

            throw new IllegalArgumentException(
                    "El plan seleccionado no está activo.");
        }

        Membresia membresia = membresiaRepository
                .findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(
                        idUsuario)
                .orElseGet(Membresia::new);

        membresia.setUsuario(cliente);
        membresia.setPlanMembresia(planSeleccionado);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaVencimiento);
        membresia.setEstado(estado);

        /*
         * Sincronización temporal con las columnas antiguas.
         * Estas columnas todavía son utilizadas por el dashboard.
         */
        membresia.setPlan(
                planSeleccionado.getNombre());

        membresia.setMonto(
                planSeleccionado.getPrecio());

        membresia.setFechaPago(fechaInicio);

        return membresiaRepository.save(membresia);
    }

    private void validarDatos(
            Integer idUsuario,
            Integer idPlan,
            LocalDate fechaInicio,
            LocalDate fechaVencimiento,
            EstadoMembresia estado) {

        if (idUsuario == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente.");
        }

        if (idPlan == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un plan.");
        }

        if (fechaInicio == null) {

            throw new IllegalArgumentException(
                    "La fecha de inicio es obligatoria.");
        }

        if (fechaVencimiento == null) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento es obligatoria.");
        }

        if (!fechaVencimiento.isAfter(fechaInicio)) {

            throw new IllegalArgumentException(
                    "La fecha de vencimiento debe ser "
                    + "posterior a la fecha de inicio.");
        }

        if (estado == null) {

            throw new IllegalArgumentException(
                    "Debe seleccionar el estado "
                    + "de la membresía.");
        }
    }
}
