package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Asistencia;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.AsistenciaRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final UsuarioRepository usuarioRepository;

    public AsistenciaService(
            AsistenciaRepository asistenciaRepository,
            UsuarioRepository usuarioRepository) {
        this.asistenciaRepository = asistenciaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Asistencia> listarPorCliente(Integer idUsuario) {
        return asistenciaRepository
                .findByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarAsistenciasDelMes(Integer idUsuario) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return asistenciaRepository.countByClienteIdUsuarioAndFechaBetween(
                idUsuario, inicioMes, finMes);
    }

    @Transactional(readOnly = true)
    public Optional<Asistencia> buscarUltimaPorCliente(Integer idUsuario) {
        return asistenciaRepository
                .findTopByClienteIdUsuarioOrderByFechaDescHoraEntradaDesc(idUsuario);
    }

    @Transactional
    public Asistencia agregar(
            Integer idUsuario,
            LocalDate fecha,
            LocalTime horaEntrada,
            LocalTime horaSalida) {
        validarDatos(fecha, horaEntrada, horaSalida);
        validarFechaDisponible(idUsuario, fecha);

        Usuario cliente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El cliente no existe."));

        Asistencia asistencia = new Asistencia();
        asistencia.setCliente(cliente);
        asistencia.setFecha(fecha);
        asistencia.setHoraEntrada(horaEntrada);
        asistencia.setHoraSalida(horaSalida);
        return asistenciaRepository.save(asistencia);
    }

    @Transactional
    public void eliminar(Integer idUsuario, Integer idAsistencia) {
        Asistencia asistencia = asistenciaRepository
                .findByIdAsistenciaAndClienteIdUsuario(idAsistencia, idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La asistencia no existe o no pertenece al cliente."));
        asistenciaRepository.delete(asistencia);
    }

    private void validarFechaDisponible(Integer idUsuario, LocalDate fecha) {
        if (asistenciaRepository.existsByClienteIdUsuarioAndFecha(idUsuario, fecha)) {
            throw new IllegalArgumentException(
                    "Ya existe una asistencia registrada para esa fecha.");
        }
    }

    private void validarDatos(
            LocalDate fecha,
            LocalTime horaEntrada,
            LocalTime horaSalida) {
        if (fecha == null || horaEntrada == null) {
            throw new IllegalArgumentException(
                    "La fecha y la hora de entrada son obligatorias.");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de asistencia no puede ser futura.");
        }
        if (horaSalida != null && horaSalida.isBefore(horaEntrada)) {
            throw new IllegalArgumentException(
                    "La hora de salida no puede ser anterior a la entrada.");
        }
    }
}
