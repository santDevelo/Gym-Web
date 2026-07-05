package com.fitsystem.controller;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.service.UsuarioService;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public AuthController(UsuarioService usuarioService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/login/cliente")
    public String loginCliente() {
        return "/auth/loginCliente";
    }

    @GetMapping("/login/entrenador")
    public String loginEntrenador() {
        return "/auth/loginEntrenador";
    }

    @GetMapping("/login/admin")
    public String loginAdmin() {
        return "/auth/loginAdmin";
    }

    @PostMapping("/login/cliente")
    public String procesarLoginCliente(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        return procesar(username, password, RolUsuario.CLIENTE, "/login/cliente", redirectAttributes);
    }

    @PostMapping("/login/entrenador")
    public String procesarLoginEntrenador(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        return procesar(username, password, RolUsuario.ENTRENADOR, "/login/entrenador", redirectAttributes);
    }

    @PostMapping("/login/admin")
    public String procesarLoginAdmin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        return procesar(username, password, RolUsuario.ADMINISTRADOR, "/login/admin", redirectAttributes);
    }

    private String procesar(String username, String password, RolUsuario rol, String rutaError, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.login(username, password, rol);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("login.error01", null, Locale.getDefault()));
            return "redirect:" + rutaError;
        }
        return "redirect:/perfil/" + usuarioOpt.get().getIdUsuario();
    }
}
