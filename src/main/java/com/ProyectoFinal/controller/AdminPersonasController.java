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
@RequestMapping("/admin")
public class AdminPersonasController {

    private final LoginService loginService;

    public AdminPersonasController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/usuarios")
    public String usuarios(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("titulo", "Usuarios");
        model.addAttribute("seccionActiva", "usuarios");
        model.addAttribute("personas", loginService.listarTodos());
        return "admin/personas";
    }

    @GetMapping("/clientes")
    public String clientes(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("titulo", "Clientes");
        model.addAttribute("seccionActiva", "clientes");
        model.addAttribute("personas", loginService.listarPorRol(Rol.CLIENTE));
        return "admin/personas";
    }

    @GetMapping("/empleados")
    public String empleados(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("titulo", "Empleados");
        model.addAttribute("seccionActiva", "empleados");
        model.addAttribute("personas", loginService.listarPorRol(Rol.ENTRENADOR));
        return "admin/personas";
    }

    private String validarAdmin(HttpSession session, Model model) {

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

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            return "redirect:/";
        }

        model.addAttribute("usuario", usuario);
        return null;
    }
}
