package com.fitsystem.controller;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.domain.Usuario;
import com.fitsystem.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Historia 16: el cliente se registra en el sistema (formulario publico, sin login/sesion real,
// ya que el material de las semanas 1-8 no cubre Spring Security).
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public RegistroController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, @RequestParam MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setActivo(true);
        usuarioService.save(usuario, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("registro.mensaje.ok", null, Locale.getDefault()));
        return "redirect:/";
    }
}
