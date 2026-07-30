package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    Optional<Membresia> findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(Integer idUsuario);

    long countByEstado(EstadoMembresia estado);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Membresia m "
            + "WHERE m.estado = :estado AND m.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal sumMontoPorEstadoEntreFechas(
            @Param("estado") EstadoMembresia estado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

}
