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
@RequestMapping("/entrenador")
public class EntrenadorExtraController {

    private final LoginService loginService;

    public EntrenadorExtraController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/rutinas")
    public String rutinas(HttpSession session, Model model) {
        String redirect = validarEntrenador(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "rutinas");
        return "entrenador/rutinas";
    }

    @GetMapping("/citas")
    public String citas(HttpSession session, Model model) {
        String redirect = validarEntrenador(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "citas");
        return "entrenador/citas";
    }

    private String validarEntrenador(HttpSession session, Model model) {

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

        if (usuario.getRol() != Rol.ENTRENADOR) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        return null;
    }
}

