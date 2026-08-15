package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Asistencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository (capa DAO) para Asistencia.
public interface AsistenciaRepository
        extends JpaRepository<Asistencia, Integer> {

    // Historial de un cliente, de la visita más reciente a la más antigua
    List<Asistencia>
            findByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(
                    Integer idUsuario);

    // Cuenta visitas dentro de un rango de fechas (usado para "asistencias del mes")
    long countByClienteIdUsuarioAndFechaBetween(
            Integer idUsuario,
            LocalDate inicio,
            LocalDate fin);

    // Evita registrar dos asistencias el mismo día para el mismo cliente
    boolean existsByClienteIdUsuarioAndFecha(
            Integer idUsuario,
            LocalDate fecha);

    // Busca por id verificando también el dueño, para que un cliente no pueda
    // borrar la asistencia de otro cambiando el id en el formulario
    Optional<Asistencia> findByIdAsistenciaAndClienteIdUsuario(
            Integer idAsistencia,
            Integer idUsuario);

    Optional<Asistencia>
            findTopByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(
                    Integer idUsuario);
}
