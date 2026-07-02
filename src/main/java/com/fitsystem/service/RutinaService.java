package com.fitsystem.service;

import com.fitsystem.domain.Rutina;
import com.fitsystem.domain.Usuario;
import com.fitsystem.repository.RutinaRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RutinaService {

    // El repositorio es final para asegurar la inmutabilidad
    private final RutinaRepository rutinaRepository;

    public RutinaService(RutinaRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }

    @Transactional(readOnly = true)
    public List<Rutina> getRutinas() {
        return rutinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rutina> getRutinasDeCliente(Integer idCliente) {
        return rutinaRepository.findByCliente_IdUsuarioOrderByFechaAsignacionDesc(idCliente);
    }

    @Transactional(readOnly = true)
    public List<Rutina> getRutinasDeEntrenador(Integer idEntrenador) {
        return rutinaRepository.findByEntrenador_IdUsuarioOrderByFechaAsignacionDesc(idEntrenador);
    }

    // Historia 11: clientes distintos asignados a un entrenador, derivado de sus rutinas
    @Transactional(readOnly = true)
    public List<Usuario> getClientesDeEntrenador(Integer idEntrenador) {
        var clientes = new LinkedHashMap<Integer, Usuario>();
        for (var rutina : getRutinasDeEntrenador(idEntrenador)) {
            clientes.putIfAbsent(rutina.getCliente().getIdUsuario(), rutina.getCliente());
        }
        return clientes.values().stream().toList();
    }

    @Transactional(readOnly = true)
    public Optional<Rutina> getRutina(Integer idRutina) {
        return rutinaRepository.findById(idRutina);
    }

    @Transactional
    public void save(Rutina rutina) {
        rutinaRepository.save(rutina);
    }

    @Transactional
    public void delete(Integer idRutina) {
        if (!rutinaRepository.existsById(idRutina)) {
            throw new IllegalArgumentException("La rutina con ID " + idRutina + " no existe.");
        }
        try {
            rutinaRepository.deleteById(idRutina);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar la rutina. Tiene datos asociados.", e);
        }
    }
}
