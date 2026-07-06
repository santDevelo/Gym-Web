package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.repository.LoginRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    private final LoginRepository usuarioRepository;

    public LoginService(LoginRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Login> login(
            String username,
            String password,
            Rol rol
    ) {

        return usuarioRepository
                .findByUsernameAndPasswordAndRol(
                        username,
                        password,
                        rol
                );
    }

    @Transactional(readOnly = true)
    public Optional<Login> buscarPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional
    public boolean registrar(Login usuario) {

        if (usuarioRepository
                .findByUsername(usuario.getUsername())
                .isPresent()) {

            return false;
        }

        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuario.setRutaImagen(null);

        usuarioRepository.save(usuario);

        return true;
    }
}