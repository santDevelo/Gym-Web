package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Ruta;
import com.ProyectoFinal.repository.RutaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Capa de servicio para Ruta. SecurityConfig la usa (con @Lazy, para evitar
// dependencia circular al construir el securityFilterChain) y arma las reglas
// de acceso por rol a partir de lo que devuelve getRutas().
@Service
public class RutaService {

    private final RutaRepository rutaRepository;

    public RutaService(
            RutaRepository rutaRepository
    ) {
        this.rutaRepository = rutaRepository;
    }

    @Transactional(readOnly = true)
    public List<Ruta> getRutas() {
        return rutaRepository
                .findAllByOrderByRequiereRolAsc();
    }
}