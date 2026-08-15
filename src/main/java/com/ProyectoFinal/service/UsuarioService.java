package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.NombreRol;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.RolRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private static final String CARPETA_IMAGENES = "usuario";

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
    public List<Usuario> listarPorRol(NombreRol rol) {
        return usuarioRepository.findDistinctByRoles_Rol(rol.name());
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarActivosPorRol(NombreRol rol) {
        return usuarioRepository.findDistinctByRoles_RolAndActivoTrue(rol.name());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public long contarPorRolActivo(NombreRol rol) {
        return usuarioRepository.countByRoles_RolAndActivoTrue(rol.name());
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional
    public Usuario guardarDesdeAdministracion(
            Usuario formulario,
            Integer idRol,
            MultipartFile imagenFile) {
        validarCamposObligatorios(formulario);
        validarDuplicados(formulario);

        Rol rol = buscarRol(idRol);
        Usuario usuario = obtenerUsuarioParaGuardar(formulario);

        copiarDatosEditables(formulario, usuario);
        usuario.setRoles(new HashSet<>(Set.of(rol)));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        guardarImagen(usuarioGuardado, imagenFile);
        return usuarioGuardado;
    }

    @Transactional
    public void cambiarEstado(Integer idUsuario, String usernameAutenticado) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        if (usuario.getUsername().equals(usernameAutenticado)) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario.");
        }

        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
    }

    private Rol buscarRol(Integer idRol) {
        if (idRol == null) {
            throw new IllegalArgumentException("Debe seleccionar un rol.");
        }

        return rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("El rol seleccionado no existe."));
    }

    private Usuario obtenerUsuarioParaGuardar(Usuario formulario) {
        if (formulario.getIdUsuario() == null) {
            return crearUsuario(formulario.getPassword());
        }

        Usuario usuario = usuarioRepository.findById(formulario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario no existe."));

        if (tieneTexto(formulario.getPassword())) {
            usuario.setPassword(passwordEncoder.encode(formulario.getPassword()));
        }
        return usuario;
    }

    private Usuario crearUsuario(String password) {
        if (!tieneTexto(password)) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        Usuario usuario = new Usuario();
        usuario.setPassword(passwordEncoder.encode(password));
        return usuario;
    }

    private void copiarDatosEditables(Usuario formulario, Usuario usuario) {
        usuario.setUsername(formulario.getUsername().trim());
        usuario.setNombre(formulario.getNombre().trim());
        usuario.setApellidos(formulario.getApellidos().trim());
        usuario.setCorreo(limpiarTextoOpcional(formulario.getCorreo()));
        usuario.setTelefono(limpiarTextoOpcional(formulario.getTelefono()));
        usuario.setActivo(formulario.isActivo());
    }

    private void validarCamposObligatorios(Usuario formulario) {
        if (formulario == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios.");
        }
        if (!tieneTexto(formulario.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (!tieneTexto(formulario.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (!tieneTexto(formulario.getApellidos())) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }
    }

    private void validarDuplicados(Usuario formulario) {
        Integer idActual = formulario.getIdUsuario();
        String username = formulario.getUsername().trim();

        if (perteneceAOtroUsuario(usuarioRepository.findByUsername(username), idActual)) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
        }

        String correo = limpiarTextoOpcional(formulario.getCorreo());
        if (correo != null
                && perteneceAOtroUsuario(usuarioRepository.findByCorreo(correo), idActual)) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
    }

    private boolean perteneceAOtroUsuario(Optional<Usuario> encontrado, Integer idActual) {
        return encontrado
                .map(usuario -> !Objects.equals(usuario.getIdUsuario(), idActual))
                .orElse(false);
    }

    private void guardarImagen(Usuario usuario, MultipartFile imagenFile) {
        if (imagenFile == null || imagenFile.isEmpty()) {
            return;
        }

        try {
            String rutaImagen = firebaseStorageService.uploadImage(
                    imagenFile,
                    CARPETA_IMAGENES,
                    usuario.getIdUsuario());
            usuario.setRutaImagen(rutaImagen);
            usuarioRepository.save(usuario);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar la imagen del usuario.", ex);
        }
    }

    private boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }

    private String limpiarTextoOpcional(String texto) {
        return tieneTexto(texto) ? texto.trim() : null;
    }
}
