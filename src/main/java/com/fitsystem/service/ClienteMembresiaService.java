package com.fitsystem.service;

import com.fitsystem.domain.ClienteMembresia;
import com.fitsystem.domain.EstadoMembresia;
import com.fitsystem.repository.ClienteMembresiaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteMembresiaService {

    // El repositorio es final para asegurar la inmutabilidad
    private final ClienteMembresiaRepository clienteMembresiaRepository;
    private final MembresiaService membresiaService;

    public ClienteMembresiaService(ClienteMembresiaRepository clienteMembresiaRepository, MembresiaService membresiaService) {
        this.clienteMembresiaRepository = clienteMembresiaRepository;
        this.membresiaService = membresiaService;
    }

    @Transactional(readOnly = true)
    public List<ClienteMembresia> getSuscripciones() {
        return clienteMembresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ClienteMembresia> getSuscripcionesDeCliente(Integer idUsuario) {
        return clienteMembresiaRepository.findByUsuario_IdUsuarioOrderByFechaInicioDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<ClienteMembresia> getSuscripcion(Integer idClienteMembresia) {
        return clienteMembresiaRepository.findById(idClienteMembresia);
    }

    @Transactional
    public void save(ClienteMembresia clienteMembresia) {
        // El formulario solo envia el id del plan elegido (membresia.idMembresia), asi que se
        // recupera la membresia completa para poder leer su duracionDias.
        var membresia = membresiaService.getMembresia(clienteMembresia.getMembresia().getIdMembresia())
                .orElseThrow(() -> new IllegalArgumentException("El plan de membresia no existe."));
        clienteMembresia.setMembresia(membresia);
        // Historia 5/6: al crear/editar la suscripcion, la fecha de vencimiento se calcula
        // a partir de la duracion (en dias) del plan de membresia elegido.
        clienteMembresia.setFechaVencimiento(clienteMembresia.getFechaInicio().plusDays(membresia.getDuracionDias()));
        clienteMembresiaRepository.save(clienteMembresia);
    }

    @Transactional
    public void extenderVencimiento(ClienteMembresia clienteMembresia, Integer dias) {
        // Historia 8: registrar un pago extiende automaticamente la membresia.
        LocalDate base = clienteMembresia.getFechaVencimiento().isBefore(LocalDate.now())
                ? LocalDate.now()
                : clienteMembresia.getFechaVencimiento();
        clienteMembresia.setFechaVencimiento(base.plusDays(dias));
        clienteMembresiaRepository.save(clienteMembresia);
    }

    @Transactional
    public void delete(Integer idClienteMembresia) {
        if (!clienteMembresiaRepository.existsById(idClienteMembresia)) {
            throw new IllegalArgumentException("La suscripcion con ID " + idClienteMembresia + " no existe.");
        }
        try {
            clienteMembresiaRepository.deleteById(idClienteMembresia);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la suscripcion. Tiene datos asociados.", e);
        }
    }

    // Historia 6/17: estado actual de la membresia (activa, vencida o pendiente de iniciar)
    public EstadoMembresia calcularEstado(ClienteMembresia clienteMembresia) {
        LocalDate hoy = LocalDate.now();
        if (clienteMembresia.getFechaInicio().isAfter(hoy)) {
            return EstadoMembresia.PENDIENTE;
        }
        if (clienteMembresia.getFechaVencimiento().isBefore(hoy)) {
            return EstadoMembresia.VENCIDA;
        }
        return EstadoMembresia.ACTIVA;
    }
}
