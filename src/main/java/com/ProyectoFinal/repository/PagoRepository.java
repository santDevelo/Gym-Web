package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso al historial de pagos ordenado desde el registro más reciente.
public interface PagoRepository
        extends JpaRepository<Pago, Integer> {

    List<Pago> findAllByOrderByFechaPagoDescIdPagoDesc();
}
