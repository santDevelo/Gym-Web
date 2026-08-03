package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.RolRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FirebaseStorageService firebaseStorageService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            FirebaseStorageService firebaseStorageService,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.firebaseStorageService = firebaseStorageService;
        this.passwordEncoder = passwordEncoder;
    }

    /*
     * Consultas de usuarios
     */

    @Transactional(readOnly = true)
    public List<Usuario> listarTodosConRoles() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(
            Integer idUsuario) {

        return usuarioRepository.findById(
                idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(
            String username) {

        return usuarioRepository.findByUsername(
                username);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarActivoPorUsername(
            String username) {

        return usuarioRepository
                .findByUsernameAndActivoTrue(
                        username);
    }

    @Transactional(readOnly = true)
    public boolean existePorUsernameOCorreo(
            String username,
            String correo) {

        return usuarioRepository
                .existsByUsernameOrCorreo(
                        username,
                        correo);
    }

    /*
     * Método utilizado por DashboardController
     */

    @Transactional(readOnly = true)
    public long contarPorRolActivo(
            String rol) {

        return usuarioRepository
                .countByRoles_RolAndActivoTrue(
                        rol);
    }

    /*
     * Consultas de roles
     */

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    /*
     * Registro público de clientes
     */

    @Transactional
    public boolean registrarCliente(
            Usuario usuario) {

        if (usuario == null) {
            return false;
        }

        if (usuario.getUsername() == null
                || usuario.getUsername().isBlank()) {

            return false;
        }

        if (usuario.getPassword() == null
                || usuario.getPassword().isBlank()) {

            return false;
        }

        Optional<Usuario> usuarioExistente
                = usuarioRepository.findByUsername(
                        usuario.getUsername().trim());

        if (usuarioExistente.isPresent()) {
            return false;
        }

        if (usuario.getCorreo() != null
                && !usuario.getCorreo().isBlank()) {

            Optional<Usuario> correoExistente
                    = usuarioRepository.findByCorreo(
                            usuario.getCorreo().trim());

            if (correoExistente.isPresent()) {
                return false;
            }
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

    /*
     * Guardar desde administración
     */

    @Transactional
    public Usuario guardarDesdeAdministracion(
            Usuario formulario,
            Integer idRol,
            MultipartFile imagenFile) {

        validarCamposObligatorios(formulario);
        validarDuplicados(formulario);

        Rol rol = rolRepository
                .findById(idRol)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El rol seleccionado no existe."));

        Usuario usuario;

        if (formulario.getIdUsuario() == null) {

            usuario = crearUsuario(formulario);

        } else {

            usuario = actualizarUsuario(formulario);
        }

        copiarDatosEditables(
                formulario,
                usuario);

        var roles = new HashSet<Rol>();
        roles.add(rol);

        usuario.setRoles(roles);

        usuario = usuarioRepository.save(usuario);

        guardarImagen(
                usuario,
                imagenFile);

        return usuario;
    }

    private Usuario crearUsuario(
            Usuario formulario) {

        if (formulario.getPassword() == null
                || formulario.getPassword().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        Usuario usuario = new Usuario();

        usuario.setPassword(
                passwordEncoder.encode(
                        formulario.getPassword()));

        return usuario;
    }

    private Usuario actualizarUsuario(
            Usuario formulario) {

        Usuario usuario = usuarioRepository
                .findById(formulario.getIdUsuario())
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El usuario no existe."));

        if (formulario.getPassword() != null
                && !formulario.getPassword().isBlank()) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            formulario.getPassword()));
        }

        return usuario;
    }

    private void copiarDatosEditables(
            Usuario formulario,
            Usuario usuario) {

        usuario.setUsername(
                formulario.getUsername().trim());

        usuario.setNombre(
                formulario.getNombre().trim());

        usuario.setApellidos(
                formulario.getApellidos().trim());

        usuario.setCorreo(
                limpiarTextoOpcional(
                        formulario.getCorreo()));

        usuario.setTelefono(
                limpiarTextoOpcional(
                        formulario.getTelefono()));

        usuario.setActivo(
                formulario.isActivo());
    }

    /*
     * Validaciones
     */

    private void validarCamposObligatorios(
            Usuario formulario) {

        if (formulario.getUsername() == null
                || formulario.getUsername().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (formulario.getNombre() == null
                || formulario.getNombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (formulario.getApellidos() == null
                || formulario.getApellidos().isBlank()) {

            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios.");
        }
    }

    private void validarDuplicados(
            Usuario formulario) {

        Integer idActual = formulario.getIdUsuario();

        usuarioRepository
                .findByUsername(
                        formulario.getUsername().trim())
                .filter(encontrado ->
                !encontrado.getIdUsuario()
                        .equals(idActual))
                .ifPresent(encontrado -> {
                    throw new IllegalArgumentException(
                            "El nombre de usuario ya está registrado.");
                });

        String correo = limpiarTextoOpcional(
                formulario.getCorreo());

        if (correo != null) {

            usuarioRepository
                    .findByCorreo(correo)
                    .filter(encontrado ->
                    !encontrado.getIdUsuario()
                            .equals(idActual))
                    .ifPresent(encontrado -> {
                        throw new IllegalArgumentException(
                                "El correo ya está registrado.");
                    });
        }
    }

    private String limpiarTextoOpcional(
            String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    /*
     * Imagen del usuario
     */

    private void guardarImagen(
            Usuario usuario,
            MultipartFile imagenFile) {

        if (imagenFile == null
                || imagenFile.isEmpty()) {

            return;
        }

        try {

            String rutaImagen
                    = firebaseStorageService.uploadImage(
                            imagenFile,
                            "usuario",
                            usuario.getIdUsuario());

            usuario.setRutaImagen(rutaImagen);

            usuarioRepository.save(usuario);

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "No se pudo guardar la imagen del usuario.",
                    ex);
        }
    }

    /*
     * Activar o desactivar
     */

    @Transactional
    public void cambiarEstado(
            Integer idUsuario,
            String usernameAutenticado) {

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El usuario no existe."));

        if (usuario.getUsername()
                .equals(usernameAutenticado)) {

            throw new IllegalArgumentException(
                    "No puedes desactivar tu propio usuario.");
        }

        usuario.setActivo(
                !usuario.isActivo());

        usuarioRepository.save(usuario);
    }
}