package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.service.LoginService;
import com.ProyectoFinal.service.MembresiaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final LoginService loginService;
    private final MembresiaService membresiaService;

    public AdminDashboardController(LoginService loginService, MembresiaService membresiaService) {
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

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("seccionActiva", "dashboard");
        model.addAttribute("clientesActivos", loginService.contarPorRolActivo(Rol.CLIENTE));
        model.addAttribute("entrenadoresActivos", loginService.contarPorRolActivo(Rol.ENTRENADOR));
        model.addAttribute("ingresosMes", membresiaService.ingresosDelMes());
        model.addAttribute("pagosPendientes", membresiaService.contarPorEstado(EstadoMembresia.PENDIENTE));

        return "admin/listado";
    }
}
