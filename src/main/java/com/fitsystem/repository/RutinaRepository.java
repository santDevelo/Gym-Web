package com.fitsystem.repository;

import com.fitsystem.domain.Rutina;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutinaRepository extends JpaRepository<Rutina, Integer> {

    //Historia 19: rutinas asignadas a un cliente
    public List<Rutina> findByCliente_IdUsuarioOrderByFechaAsignacionDesc(Integer idCliente);

    //Historia 11 y 12: rutinas asignadas por un entrenador (de ahi se derivan sus clientes)
    public List<Rutina> findByEntrenador_IdUsuarioOrderByFechaAsignacionDesc(Integer idEntrenador);
}
