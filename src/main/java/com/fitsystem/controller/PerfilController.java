package com.fitsystem.controller;

import com.fitsystem.service.ClienteMembresiaService;
import com.fitsystem.service.RutinaService;
import com.fitsystem.service.UsuarioService;
import java.util.HashMap;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Historias 11, 17 y 19: vistas de "autoservicio" para cliente/entrenador.
// El material de las semanas 1-8 no cubre login/sesion, asi que el usuario se identifica
// navegando a /perfil/{idUsuario}/..., replicando el patron de IndexController de la semana 6
// (GET /consultas/{idCategoria}), en vez de inventar un sistema de autenticacion.
@Controller
public class PerfilController {

    private final UsuarioService usuarioService;
    private final ClienteMembresiaService clienteMembresiaService;
    private final RutinaService rutinaService;
    private final MessageSource messageSource;

    public PerfilController(UsuarioService usuarioService, ClienteMembresiaService clienteMembresiaService,
            RutinaService rutinaService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.clienteMembresiaService = clienteMembresiaService;
        this.rutinaService = rutinaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/perfil/{idUsuario}/membresia")
    public String membresia(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuarioSeleccionado", usuarioOpt.get());
        var suscripciones = clienteMembresiaService.getSuscripcionesDeCliente(idUsuario);
        model.addAttribute("suscripciones", suscripciones);
        var estados = new HashMap<Integer, String>();
        for (var s : suscripciones) {
            estados.put(s.getIdClienteMembresia(), clienteMembresiaService.calcularEstado(s).name());
        }
        model.addAttribute("estados", estados);
        return "/perfil/membresia";
    }

    @GetMapping("/perfil/{idUsuario}/rutinas")
    public String rutinas(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuarioSeleccionado", usuarioOpt.get());
        model.addAttribute("rutinas", rutinaService.getRutinasDeCliente(idUsuario));
        return "/perfil/rutinas";
    }

    @GetMapping("/perfil/{idUsuario}/clientes")
    public String clientes(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return "redirect:/usuario/listado";
        }
        model.addAttribute("usuarioSeleccionado", usuarioOpt.get());
        model.addAttribute("clientesAsignados", rutinaService.getClientesDeEntrenador(idUsuario));
        return "/perfil/clientes";
    }
}
