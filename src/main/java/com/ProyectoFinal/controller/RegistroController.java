package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Registro publico de clientes (prototipo de Figma: pantalla "Inscribirse").
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String nuevo() {
        return "/registro/nuevo";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario, @RequestParam MultipartFile imagenFile, RedirectAttributes redirectAttributes) {
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setActivo(true);
        usuarioService.save(usuario, imagenFile);
        redirectAttributes.addFlashAttribute("todoOk", "Su registro se realizó correctamente.");
        return "redirect:/";
    }
}
