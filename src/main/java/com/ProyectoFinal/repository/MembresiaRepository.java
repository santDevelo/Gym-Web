package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Acceso a membresías y consultas utilizadas por los indicadores administrativos.
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    Optional<Membresia> findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(Integer idUsuario);

    long countByEstado(EstadoMembresia estado);

    // Devuelve solamente el registro más reciente de cada cliente.
    @Query("SELECT m FROM Membresia m "
            + "WHERE m.planMembresia IS NOT NULL "
            + "AND m.idMembresia = ("
            + "SELECT MAX(m2.idMembresia) FROM Membresia m2 "
            + "WHERE m2.usuario = m.usuario) "
            + "ORDER BY m.usuario.nombre, m.usuario.apellidos")
    List<Membresia> findMembresiasActuales();

    // Suma los montos de un estado dentro de un intervalo de fechas.
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Membresia m "
            + "WHERE m.estado = :estado AND m.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal sumMontoPorEstadoEntreFechas(
            @Param("estado") EstadoMembresia estado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

}
