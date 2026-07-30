package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.service.LoginService;
import com.ProyectoFinal.service.MembresiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteDashboardController {

    private final LoginService loginService;
    private final MembresiaService membresiaService;

    public ClienteDashboardController(
            LoginService loginService,
            MembresiaService membresiaService
    ) {
        this.loginService = loginService;
        this.membresiaService = membresiaService;
    }

    @GetMapping({"", "/", "/listado"})
    public String mostrarDashboard(
            HttpSession session,
            Model model
    ) {

        Integer idUsuario =
                (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            return "redirect:/login";
        }

        var usuarioOptional =
                loginService.buscarPorId(idUsuario);

        if (usuarioOptional.isEmpty()) {

            session.invalidate();

            return "redirect:/login";
        }

        Login usuario = usuarioOptional.get();

        if (usuario.getRol() != Rol.CLIENTE) {
            return "redirect:/";
        }

        Membresia membresia = membresiaService.buscarUltimaPorUsuario(idUsuario).orElse(null);

        model.addAttribute("usuario", usuario);
        model.addAttribute("seccionActiva", "inicio");
        model.addAttribute("membresia", membresia);

        return "cliente/listado";
    }
}
