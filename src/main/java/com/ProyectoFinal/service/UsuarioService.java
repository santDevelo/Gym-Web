package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.RolRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(
            boolean soloActivos
    ) {

        List<Usuario> usuarios;

        if (soloActivos) {
            usuarios = usuarioRepository.findByActivoTrue();
        } else {
            usuarios = usuarioRepository.findAll();
        }

        usuarios.forEach(
                usuario -> usuario.getRoles().size()
        );

        return usuarios;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(
            Integer idUsuario
    ) {

        Optional<Usuario> usuario =
                usuarioRepository.findById(idUsuario);

        usuario.ifPresent(
                encontrado -> encontrado.getRoles().size()
        );

        return usuario;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(
            String username
    ) {

        Optional<Usuario> usuario =
                usuarioRepository.findByUsername(username);

        usuario.ifPresent(
                encontrado -> encontrado.getRoles().size()
        );

        return usuario;
    }

    @Transactional(readOnly = true)
    public long contarPorRolActivo(
            String rol
    ) {
        return usuarioRepository.contarActivosPorRol(rol);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(
            String rol
    ) {

        List<Usuario> usuarios =
                usuarioRepository.listarPorNombreRol(rol);

        usuarios.forEach(
                usuario -> usuario.getRoles().size()
        );

        return usuarios;
    }

    @Transactional(readOnly = true)
    public boolean existeUsuario(
            String username,
            String correo
    ) {
        return usuarioRepository
                .existsByUsernameOrCorreo(
                        username,
                        correo
                );
    }

    @Transactional
    public boolean registrarCliente(
            Usuario usuario
    ) {

        if (existeUsuario(
                usuario.getUsername(),
                usuario.getCorreo()
        )) {
            return false;
        }

        Rol rolCliente = rolRepository
                .findByRol("CLIENTE")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "El rol CLIENTE no existe."
                        )
                );

        usuario.setPassword(
                passwordEncoder.encode(
                        usuario.getPassword()
                )
        );

        usuario.setActivo(true);
        usuario.setRutaImagen(null);
        usuario.getRoles().clear();
        usuario.getRoles().add(rolCliente);

        usuarioRepository.save(usuario);

        return true;
    }
}