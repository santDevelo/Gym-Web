package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Rutina;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository
        extends JpaRepository<Rutina, Integer> {

    Optional<Rutina>
            findTopByClienteIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(
                    Integer idUsuario);

    boolean existsByClienteIdUsuarioAndActivaTrue(
            Integer idUsuario);

    List<Rutina> findByActivaTrueOrderByClienteNombreAscFechaAsignacionDesc();
}
