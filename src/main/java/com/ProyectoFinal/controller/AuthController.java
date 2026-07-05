package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Pantallas de login del prototipo de Figma (cliente/entrenador/administrador), separadas por rol.
// Se valida username/password/rol directamente contra la tabla usuario, y al validar se
// redirige al dashboard de autoservicio de /perfil/{idUsuario}.
@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos.");
            return "redirect:" + rutaError;
        }
        return "redirect:/perfil/" + usuarioOpt.get().getIdUsuario();
    }
}
