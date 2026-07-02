package com.fitsystem.repository;

import com.fitsystem.domain.Pago;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    //Consulta derivada que recupera los pagos en un rango de fechas y los ordena por fecha ascendente
    public List<Pago> findByFechaPagoBetweenOrderByFechaPagoAsc(LocalDate fechaInf, LocalDate fechaSup);

    //Consulta JPQL que recupera los pagos en un rango de fechas y los ordena por fecha ascendente
    @Query(value = "SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :fechaInf AND :fechaSup ORDER BY p.fechaPago ASC")
    public List<Pago> consultaJPQL(@Param("fechaInf") LocalDate fechaInf, @Param("fechaSup") LocalDate fechaSup);

    //Consulta SQL que recupera los pagos en un rango de fechas y los ordena por fecha ascendente
    @Query(nativeQuery = true,
            value = "SELECT * FROM pago p WHERE p.fecha_pago BETWEEN :fechaInf AND :fechaSup ORDER BY p.fecha_pago ASC")
    public List<Pago> consultaSQL(@Param("fechaInf") LocalDate fechaInf, @Param("fechaSup") LocalDate fechaSup);
}
