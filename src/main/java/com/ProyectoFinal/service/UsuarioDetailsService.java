package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Implementación de UserDetailsService que Spring Security usa para
// autenticar contra la tabla usuario en BD (en vez del InMemoryUserDetailsManager
// de las primeras clases de seguridad). Se registra con el nombre de bean
// "userDetailsService" para que Spring Security lo tome automáticamente.
@Service("userDetailsService")
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final HttpSession session;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository,
            HttpSession session
    ) {
        this.usuarioRepository = usuarioRepository;
        this.session = session;
    }

    // Spring Security llama este método en cada intento de login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(
            String username
    ) throws UsernameNotFoundException {

        // Solo usuarios activos pueden autenticarse
        Usuario usuario = usuarioRepository
                .findByUsernameAndActivoTrue(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuario no encontrado: "
                                + username
                        )
                );

        // Guarda la foto de perfil en la sesión para no consultarla en cada
        // petición (se usa en el header del dashboard)
        session.removeAttribute("imagenUsuario");
        session.setAttribute(
                "imagenUsuario",
                usuario.getRutaImagen()
        );

        // Spring Security espera los roles con el prefijo "ROLE_"
        var autoridades = usuario.getRoles()
                .stream()
                .map(rol ->
                        new SimpleGrantedAuthority(
                                "ROLE_" + rol.getRol()
                        )
                )
                .collect(Collectors.toSet());

        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                autoridades
        );
    }
}