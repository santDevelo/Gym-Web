package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> login(String username,
                                   String password,
                                   RolUsuario rol) {

        return usuarioRepository.findByUsernameAndPasswordAndRol(
                username,
                password,
                rol);
    }

    @Transactional
    public boolean registrar(Usuario usuario) {

        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            return false;
        }

        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setActivo(true);
        usuario.setRutaImagen(null);

        usuarioRepository.save(usuario);

        return true;
    }

}