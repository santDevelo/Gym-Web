package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rutina;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository (capa DAO) para Rutina.
public interface RutinaRepository
        extends JpaRepository<Rutina, Integer> {

    // La rutina activa más reciente de un cliente (solo puede haber una a la vez)
    Optional<Rutina>
            findTopByClienteIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(
                    Integer idUsuario);

    // Para saber si un cliente ya tiene rutina activa antes de crearle otra
    boolean existsByClienteIdUsuarioAndActivaTrue(
            Integer idUsuario);

    List<Rutina> findByActivaTrueOrderByClienteNombreAscFechaAsignacionDesc();
}
