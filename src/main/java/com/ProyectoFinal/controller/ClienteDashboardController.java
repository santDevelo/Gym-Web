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
public class ClienteDashboardController {

    private final LoginService loginService;

    public ClienteDashboardController(
            LoginService loginService
    ) {
        this.loginService = loginService;
    }

    @GetMapping({"", "/", "/listado"})
    public String mostrarDashboard(
            HttpSession session,
            Model model
    ) {

        Integer idUsuario =
                (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            return "redirect:/login/cliente";
        }

        var usuarioOptional =
                loginService.buscarPorId(idUsuario);

        if (usuarioOptional.isEmpty()) {

            session.invalidate();

            return "redirect:/login/cliente";
        }

        Login usuario = usuarioOptional.get();

        if (usuario.getRol() != Rol.CLIENTE) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);

        return "cliente/listado";
    }
}