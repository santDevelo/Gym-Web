package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.repository.MembresiaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> buscarUltimaPorUsuario(Integer idUsuario) {
        return membresiaRepository.findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(idUsuario);
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoMembresia estado) {
        return membresiaRepository.countByEstado(estado);
    }

    @Transactional(readOnly = true)
    public BigDecimal ingresosDelMes() {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.withDayOfMonth(1);
        LocalDate fin = hoy.withDayOfMonth(hoy.lengthOfMonth());
        return membresiaRepository.sumMontoPorEstadoEntreFechas(EstadoMembresia.ACTIVA, inicio, fin);
    }

    @Transactional(readOnly = true)
    public List<Membresia> listarTodas() {
        return membresiaRepository.findAll();
    }
}
