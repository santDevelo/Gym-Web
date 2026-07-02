package com.fitsystem.service;

import com.fitsystem.domain.Membresia;
import com.fitsystem.repository.MembresiaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembresiaService {

    // El repositorio es final para asegurar la inmutabilidad
    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Membresia> getMembresias(boolean activo) {
        if (activo) { //Solo activas...
            return membresiaRepository.findByActivoTrue();
        }
        return membresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> getMembresia(Integer idMembresia) {
        return membresiaRepository.findById(idMembresia);
    }

    @Transactional
    public void save(Membresia membresia) {
        membresiaRepository.save(membresia);
    }

    @Transactional
    public void delete(Integer idMembresia) {
        // Verifica si la membresia existe antes de intentar eliminarla
        if (!membresiaRepository.existsById(idMembresia)) {
            // Lanza una excepcion para indicar que la membresia no fue encontrada
            throw new IllegalArgumentException("La membresia con ID " + idMembresia + " no existe.");
        }
        try {
            membresiaRepository.deleteById(idMembresia);
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepcion para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar la membresia. Tiene datos asociados.", e);
        }
    }
}
