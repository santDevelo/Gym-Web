package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteExtraController {

    private final LoginService loginService;

    public ClienteExtraController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/rutina")
    public String rutina(HttpSession session, Model model) {
        String redirect = validarCliente(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "rutina");
        return "cliente/rutina";
    }

    @GetMapping("/reservar-cita")
    public String reservarCita(HttpSession session, Model model) {
        String redirect = validarCliente(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "citas");
        return "cliente/reservarcita";
    }

    @GetMapping("/asistencia")
    public String asistencia(HttpSession session, Model model) {
        String redirect = validarCliente(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "asistencia");
        return "cliente/asistencia";
    }

    @GetMapping("/notificaciones")
    public String notificaciones(HttpSession session, Model model) {
        String redirect = validarCliente(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "notificaciones");
        return "cliente/notificaciones";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        String redirect = validarCliente(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "perfil");
        return "cliente/perfil";
    }

    private String validarCliente(HttpSession session, Model model) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        var usuarioOptional = loginService.buscarPorId(idUsuario);

        if (usuarioOptional.isEmpty()) {
            session.invalidate();
            return "redirect:/login";
        }

        Login usuario = usuarioOptional.get();

        if (usuario.getRol() != Rol.CLIENTE) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        return null;
    }
}

