package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.RolRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.util.HashSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistroService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public boolean crearUsuario(Usuario usuario) {

        if (!datosValidos(usuario)
                || existeUsuario(usuario)) {

            return false;
        }

        Rol rolCliente = rolRepository
                .findByRol("CLIENTE")
                .orElseThrow(() ->
                new IllegalStateException(
                        "El rol CLIENTE no existe."));

        usuario.setIdUsuario(null);

        usuario.setUsername(
                usuario.getUsername().trim());

        usuario.setPassword(
                passwordEncoder.encode(
                        usuario.getPassword()));

        usuario.setNombre(
                usuario.getNombre().trim());

        usuario.setApellidos(
                usuario.getApellidos().trim());

        usuario.setCorreo(
                limpiarTextoOpcional(
                        usuario.getCorreo()));

        usuario.setTelefono(
                limpiarTextoOpcional(
                        usuario.getTelefono()));

        usuario.setRutaImagen(null);
        usuario.setActivo(true);

        var roles = new HashSet<Rol>();
        roles.add(rolCliente);

        usuario.setRoles(roles);

        usuarioRepository.save(usuario);

        return true;
    }

    private boolean datosValidos(
            Usuario usuario) {

        return usuario != null
                && tieneTexto(usuario.getUsername())
                && tieneTexto(usuario.getPassword())
                && tieneTexto(usuario.getNombre())
                && tieneTexto(usuario.getApellidos());
    }

    private boolean existeUsuario(
            Usuario usuario) {

        boolean existeUsername
                = usuarioRepository
                        .findByUsername(
                                usuario
                                        .getUsername()
                                        .trim())
                        .isPresent();

        if (existeUsername) {
            return true;
        }

        String correo = limpiarTextoOpcional(
                usuario.getCorreo());

        return correo != null
                && usuarioRepository
                        .findByCorreo(correo)
                        .isPresent();
    }

    private boolean tieneTexto(String texto) {

        return texto != null
                && !texto.isBlank();
    }

    private String limpiarTextoOpcional(
            String texto) {

        return tieneTexto(texto)
                ? texto.trim()
                : null;
    }
}