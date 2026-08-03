package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Usuario;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

    private final UsuarioService usuarioService;

    public RegistroService(
            UsuarioService usuarioService
    ) {
        this.usuarioService = usuarioService;
    }

    public boolean crearUsuario(
            Usuario usuario
    ) {
        return usuarioService.registrarCliente(usuario);
    }
}