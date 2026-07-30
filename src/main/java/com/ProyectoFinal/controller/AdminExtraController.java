package com.ProyectoFinal.controller;

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
public class AdminExtraController {

    private final LoginService loginService;
    private final MembresiaService membresiaService;

    public AdminExtraController(LoginService loginService, MembresiaService membresiaService) {
        this.loginService = loginService;
        this.membresiaService = membresiaService;
    }

    @GetMapping("/membresias")
    public String membresias(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "membresias");
        return "admin/membresias";
    }

    @GetMapping("/pagos")
    public String pagos(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "pagos");
        model.addAttribute("pagos", membresiaService.listarTodas());
        return "admin/pagos";
    }

    @GetMapping("/reportes")
    public String reportes(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "reportes");
        return "admin/reportes";
    }

    @GetMapping("/configuracion")
    public String configuracion(HttpSession session, Model model) {
        String redirect = validarAdmin(session, model);
        if (redirect != null) {
            return redirect;
        }
        model.addAttribute("seccionActiva", "configuracion");
        return "admin/configuracion";
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
