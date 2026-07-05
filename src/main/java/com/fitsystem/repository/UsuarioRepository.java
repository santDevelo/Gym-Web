package com.fitsystem.repository;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public List<Usuario> findByActivoTrue();

    public List<Usuario> findByRolOrderByNombreAsc(RolUsuario rol);

    public List<Usuario> findByRolAndActivoTrueOrderByNombreAsc(RolUsuario rol);

    public Optional<Usuario> findByUsernameAndPasswordAndRol(String username, String password, RolUsuario rol);
}
