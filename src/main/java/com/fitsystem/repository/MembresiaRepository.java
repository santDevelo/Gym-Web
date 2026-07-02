package com.fitsystem.repository;

import com.fitsystem.domain.Membresia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    public List<Membresia> findByActivoTrue();
}
