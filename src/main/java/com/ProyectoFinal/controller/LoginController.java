package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final LoginService usuarioService;

    public LoginController(LoginService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/acceso")
    public String acceso() {
        return "auth/acceso";
    }

    @GetMapping("/registro")
    public String registro() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {

        Login usuario = new Login();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setUsername(username);
        usuario.setPassword(password);

        boolean registrado = usuarioService.registrar(usuario);

        if (!registrado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario ya existe."
            );

            return "redirect:/registro";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cuenta creada correctamente."
        );

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String entrar(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        var usuarioOpt = usuarioService.login(username, password);

        if (usuarioOpt.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Usuario o contraseña incorrectos."
            );

            return "redirect:/login";
        }

        Login usuario = usuarioOpt.get();

        session.setAttribute(
                "idUsuario",
                usuario.getIdUsuario()
        );

        return switch (usuario.getRol()) {
            case ADMINISTRADOR -> "redirect:/admin";
            case ENTRENADOR -> "redirect:/entrenador";
            case CLIENTE -> "redirect:/cliente";
        };
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}
