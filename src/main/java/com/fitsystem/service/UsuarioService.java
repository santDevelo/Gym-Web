package com.fitsystem.service;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.domain.Usuario;
import com.fitsystem.repository.UsuarioRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    // El repositorio es final para asegurar la inmutabilidad
    private final UsuarioRepository usuarioRepository;
    private final FirebaseStorageService firebaseStorageService;

    public UsuarioService(UsuarioRepository usuarioRepository, FirebaseStorageService firebaseStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) { //Solo activos...
            return usuarioRepository.findByActivoTrue();
        }
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosPorRol(RolUsuario rol) {
        return usuarioRepository.findByRolOrderByNombreAsc(rol);
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuariosActivosPorRol(RolUsuario rol) {
        return usuarioRepository.findByRolAndActivoTrueOrderByNombreAsc(rol);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional
    public void save(Usuario usuario, MultipartFile imagenFile) {
        usuario = usuarioRepository.save(usuario);
        if (!imagenFile.isEmpty()) { //Si no esta vacio... pasaron una imagen...
            try {
                String rutaImagen = firebaseStorageService.uploadImage(
                        imagenFile, "usuario",
                        usuario.getIdUsuario());
                usuario.setRutaImagen(rutaImagen);
                usuarioRepository.save(usuario);
            } catch (IOException e) {

            }
        }
    }

    @Transactional
    public void delete(Integer idUsuario) {
        // Verifica si el usuario existe antes de intentar eliminarlo
        if (!usuarioRepository.existsById(idUsuario)) {
            // Lanza una excepcion para indicar que el usuario no fue encontrado
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            // Lanza una nueva excepcion para encapsular el problema de integridad de datos
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }
}
