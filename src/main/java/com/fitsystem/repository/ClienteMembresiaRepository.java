package com.fitsystem.repository;

import com.fitsystem.domain.ClienteMembresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteMembresiaRepository extends JpaRepository<ClienteMembresia, Integer> {

    //Consulta derivada que recupera las suscripciones de un cliente, la mas reciente primero
    public List<ClienteMembresia> findByUsuario_IdUsuarioOrderByFechaInicioDesc(Integer idUsuario);
}
