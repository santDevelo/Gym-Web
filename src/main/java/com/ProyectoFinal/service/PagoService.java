package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.MetodoPago;
import com.ProyectoFinal.domain.NombreRol;
import com.ProyectoFinal.domain.Pago;
import com.ProyectoFinal.repository.MembresiaRepository;
import com.ProyectoFinal.repository.PagoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MembresiaRepository membresiaRepository;

    public PagoService(
            PagoRepository pagoRepository,
            MembresiaRepository membresiaRepository) {
        this.pagoRepository = pagoRepository;
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Pago> listarHistorial() {
        return pagoRepository.findAllByOrderByFechaPagoDescIdPagoDesc();
    }

    @Transactional
    public Pago registrar(
            Integer idMembresia,
            LocalDate fechaProximoPago,
            BigDecimal monto,
            MetodoPago metodoPago) {
        validarDatos(idMembresia, fechaProximoPago, monto, metodoPago);

        Membresia membresia = obtenerMembresiaValida(idMembresia);
        LocalDate fechaPago = LocalDate.now();
        Pago pago = crearPago(
                membresia, fechaPago, fechaProximoPago, monto, metodoPago);

        actualizarMembresia(membresia, fechaPago, fechaProximoPago, monto);
        membresiaRepository.save(membresia);
        return pagoRepository.save(pago);
    }

    @Transactional
    public void eliminar(Integer idPago) {
        if (idPago == null || !pagoRepository.existsById(idPago)) {
            throw new IllegalArgumentException("El pago no existe.");
        }
        pagoRepository.deleteById(idPago);
    }

    private Membresia obtenerMembresiaValida(Integer idMembresia) {
        Membresia membresia = membresiaRepository.findById(idMembresia)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La membresía seleccionada no existe."));
        if (membresia.getUsuario() == null
                || !membresia.getUsuario().tieneRol(NombreRol.CLIENTE)) {
            throw new IllegalArgumentException(
                    "La membresía no pertenece a un cliente válido.");
        }
        if (membresia.getPlanMembresia() == null) {
            throw new IllegalArgumentException("El cliente no tiene un plan relacionado.");
        }
        return membresia;
    }

    private Pago crearPago(
            Membresia membresia,
            LocalDate fechaPago,
            LocalDate fechaProximoPago,
            BigDecimal monto,
            MetodoPago metodoPago) {
        Pago pago = new Pago();
        pago.setUsuario(membresia.getUsuario());
        pago.setPlanMembresia(membresia.getPlanMembresia());
        pago.setFechaPago(fechaPago);
        pago.setFechaProximoPago(fechaProximoPago);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        return pago;
    }

    private void actualizarMembresia(
            Membresia membresia,
            LocalDate fechaPago,
            LocalDate fechaProximoPago,
            BigDecimal monto) {
        membresia.setFechaPago(fechaPago);
        membresia.setFechaVencimiento(fechaProximoPago);
        membresia.setEstado(EstadoMembresia.ACTIVA);
        membresia.setMonto(monto);
        membresia.setPlan(membresia.getPlanMembresia().getNombre());
        if (membresia.getFechaInicio() == null) {
            membresia.setFechaInicio(fechaPago);
        }
    }

    private void validarDatos(
            Integer idMembresia,
            LocalDate fechaProximoPago,
            BigDecimal monto,
            MetodoPago metodoPago) {
        if (idMembresia == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente.");
        }
        if (fechaProximoPago == null) {
            throw new IllegalArgumentException("La fecha del próximo pago es obligatoria.");
        }
        if (!fechaProximoPago.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha del próximo pago debe ser futura.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if (metodoPago == null) {
            throw new IllegalArgumentException("Debe seleccionar la forma de pago.");
        }
    }
}
