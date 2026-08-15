package com.ProyectoFinal.repository;

import com.ProyectoFinal.domain.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Repository (capa DAO) para Home. No necesita consultas propias: con los
// métodos que trae JpaRepository (findById, save, etc.) alcanza.
@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {
}
