package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FirebaseStorageService firebaseStorageService;

    public UsuarioService(UsuarioRepository usuarioRepository, FirebaseStorageService firebaseStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
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

    @Transactional(readOnly = true)
    public Optional<Usuario> login(String username, String password, RolUsuario rol) {
        return usuarioRepository.findByUsernameAndPasswordAndRol(username, password, rol);
    }

    @Transactional
    public void save(Usuario usuario, MultipartFile imagenFile) {
        usuario = usuarioRepository.save(usuario);
        if (!imagenFile.isEmpty()) {
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
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException("El usuario con ID " + idUsuario + " no existe.");
        }
        try {
            usuarioRepository.deleteById(idUsuario);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("No se puede eliminar el usuario. Tiene datos asociados.", e);
        }
    }
}
