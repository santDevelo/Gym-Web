package com.fitsystem.service;

import com.fitsystem.domain.Pago;
import com.fitsystem.repository.PagoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagoService {

    // El repositorio es final para asegurar la inmutabilidad
    private final PagoRepository pagoRepository;
    private final ClienteMembresiaService clienteMembresiaService;

    public PagoService(PagoRepository pagoRepository, ClienteMembresiaService clienteMembresiaService) {
        this.pagoRepository = pagoRepository;
        this.clienteMembresiaService = clienteMembresiaService;
    }

    @Transactional(readOnly = true)
    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Pago> getPago(Integer idPago) {
        return pagoRepository.findById(idPago);
    }

    @Transactional
    public void save(Pago pago) {
        // El formulario solo envia el id de la suscripcion (clienteMembresia.idClienteMembresia),
        // asi que se recupera la suscripcion completa (con su plan) antes de guardar el pago.
        var clienteMembresia = clienteMembresiaService.getSuscripcion(pago.getClienteMembresia().getIdClienteMembresia())
                .orElseThrow(() -> new IllegalArgumentException("La suscripcion no existe."));
        pago.setClienteMembresia(clienteMembresia);
        pagoRepository.save(pago);
        // Historia 8: registrar un pago manualmente actualiza (extiende) la membresia asociada.
        clienteMembresiaService.extenderVencimiento(clienteMembresia, clienteMembresia.getMembresia().getDuracionDias());
    }

    @Transactional
    public void delete(Integer idPago) {
        if (!pagoRepository.existsById(idPago)) {
            throw new IllegalArgumentException("El pago con ID " + idPago + " no existe.");
        }
        try {
            pagoRepository.deleteById(idPago);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el pago. Tiene datos asociados.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Pago> consultaDerivada(LocalDate fechaInf, LocalDate fechaSup) {
        return pagoRepository.findByFechaPagoBetweenOrderByFechaPagoAsc(fechaInf, fechaSup);
    }

    @Transactional(readOnly = true)
    public List<Pago> consultaJPQL(LocalDate fechaInf, LocalDate fechaSup) {
        return pagoRepository.consultaJPQL(fechaInf, fechaSup);
    }

    @Transactional(readOnly = true)
    public List<Pago> consultaSQL(LocalDate fechaInf, LocalDate fechaSup) {
        return pagoRepository.consultaSQL(fechaInf, fechaSup);
    }
}
