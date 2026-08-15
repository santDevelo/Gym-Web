package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.RolRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

// Capa de servicio: reglas de negocio de Usuario (alta, edición, activar/
// desactivar). El controlador nunca habla directo con el repository; siempre
// pasa por aquí, que valida y delega en UsuarioRepository.
@Service
public class UsuarioService {

    // Todas las dependencias son "private final" e inyectadas por constructor
    // (sin @Autowired en campo), como se vio en clase.
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

    @Transactional(readOnly = true)
    public List<Usuario> listarTodosConRoles() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarPorRol(String rol) {

        return usuarioRepository
                .findDistinctByRoles_Rol(rol);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarActivosPorRol(
            String rol) {

        return usuarioRepository
                .findDistinctByRoles_RolAndActivoTrue(rol);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(
            Integer idUsuario) {

        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(
            String username) {

        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarActivoPorUsername(
            String username) {

        return usuarioRepository
                .findByUsernameAndActivoTrue(username);
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

    @Transactional(readOnly = true)
    public long contarPorRolActivo(String rol) {

        return usuarioRepository
                .countByRoles_RolAndActivoTrue(rol);
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    // Alta o edición de usuario desde el panel de administración. Distingue
    // "crear" de "editar" según si formulario.idUsuario viene null (patrón
    // visto en clase: alta vs. edición por id == null).
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

        Usuario usuario
                = obtenerUsuarioParaGuardar(formulario);

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

    // Si no hay id es un usuario nuevo; si hay id, se recupera el existente
    // y solo se re-encripta la contraseña si el campo llegó con texto (dejarlo
    // en blanco al editar significa "no cambiar la contraseña").
    private Usuario obtenerUsuarioParaGuardar(
            Usuario formulario) {

        if (formulario.getIdUsuario() == null) {

            return crearUsuario(
                    formulario.getPassword());
        }

        Usuario usuario = usuarioRepository
                .findById(formulario.getIdUsuario())
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El usuario no existe."));

        if (tieneTexto(formulario.getPassword())) {

            usuario.setPassword(
                    passwordEncoder.encode(
                            formulario.getPassword()));
        }

        return usuario;
    }

    // La contraseña siempre se guarda cifrada con BCrypt (PasswordEncoder),
    // nunca en texto plano
    private Usuario crearUsuario(String password) {

        if (!tieneTexto(password)) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        Usuario usuario = new Usuario();

        usuario.setPassword(
                passwordEncoder.encode(password));

        return usuario;
    }

    private void copiarDatosEditables(
            Usuario formulario,Usuario usuario) {
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
    private void validarCamposObligatorios(
            Usuario formulario) {

        if (!tieneTexto(formulario.getUsername())) {

            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (!tieneTexto(formulario.getNombre())) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (!tieneTexto(formulario.getApellidos())) {

            throw new IllegalArgumentException(
                    "Los apellidos son obligatorios.");
        }
    }

    private void validarDuplicados(
            Usuario formulario) {

        Integer idActual = formulario.getIdUsuario();

        Optional<Usuario> mismoUsername
                = usuarioRepository.findByUsername(
                        formulario
                                .getUsername()
                                .trim());

        if (perteneceAOtroUsuario(
                mismoUsername,
                idActual)) {

            throw new IllegalArgumentException(
                    "El nombre de usuario ya está registrado.");
        }

        String correo = limpiarTextoOpcional(
                formulario.getCorreo());

        if (correo == null) {
            return;
        }

        Optional<Usuario> mismoCorreo
                = usuarioRepository.findByCorreo(correo);

        if (perteneceAOtroUsuario(
                mismoCorreo,
                idActual)) {

            throw new IllegalArgumentException(
                    "El correo ya está registrado.");
        }
    }

    private boolean perteneceAOtroUsuario(
            Optional<Usuario> encontrado,
            Integer idActual) {

        return encontrado.isPresent()
                && !Objects.equals(
                        encontrado.get().getIdUsuario(),
                        idActual);
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

    // Si no vino archivo, no hace nada (la imagen es opcional). Si vino, la
    // sube a Firebase Storage y guarda la URL resultante en el usuario.
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

    // Activa o desactiva (borrado lógico) un usuario. No deja que el admin
    // se desactive a sí mismo para evitar quedarse fuera del sistema.
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
