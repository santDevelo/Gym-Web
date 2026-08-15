package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository (capa DAO) para Pago.
public interface PagoRepository
        extends JpaRepository<Pago, Integer> {

    // Historial completo, del pago más reciente al más antiguo
    List<Pago> findAllByOrderByFechaPagoDescIdPagoDesc();
}
