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

// Repository (capa DAO) para Membresia. Combina consultas derivadas simples
// con JPQL (@Query) cuando la lógica no se puede expresar solo con el nombre
// del método (ver semana8 del curso: preferir derivada, JPQL si no alcanza).
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    // Última membresía registrada para un cliente (la vigente)
    Optional<Membresia> findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(Integer idUsuario);

    long countByEstado(EstadoMembresia estado);

    // Trae, para cada usuario, únicamente su membresía más reciente (subconsulta
    // con MAX(idMembresia)) para no listar el historial completo en pantalla
    @Query("SELECT m FROM Membresia m "
            + "WHERE m.planMembresia IS NOT NULL "
            + "AND m.idMembresia = ("
            + "SELECT MAX(m2.idMembresia) FROM Membresia m2 "
            + "WHERE m2.usuario = m.usuario) "
            + "ORDER BY m.usuario.nombre, m.usuario.apellidos")
    List<Membresia> findMembresiasActuales();

    // Suma de montos pagados en un rango de fechas; COALESCE evita que SUM()
    // devuelva null cuando no hay filas que cumplan la condición
    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM Membresia m "
            + "WHERE m.estado = :estado AND m.fechaPago BETWEEN :inicio AND :fin")
    BigDecimal sumMontoPorEstadoEntreFechas(
            @Param("estado") EstadoMembresia estado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);

}
