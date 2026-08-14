package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Asistencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsistenciaRepository
        extends JpaRepository<Asistencia, Integer> {

    List<Asistencia>
            findByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(
                    Integer idUsuario);

    long countByClienteIdUsuarioAndFechaBetween(
            Integer idUsuario,
            LocalDate inicio,
            LocalDate fin);

    boolean existsByClienteIdUsuarioAndFecha(
            Integer idUsuario,
            LocalDate fecha);

    Optional<Asistencia> findByIdAsistenciaAndClienteIdUsuario(
            Integer idAsistencia,
            Integer idUsuario);

    Optional<Asistencia>
            findTopByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(
                    Integer idUsuario);
}
