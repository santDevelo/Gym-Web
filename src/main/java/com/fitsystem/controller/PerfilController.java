package com.fitsystem.controller;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.service.ClienteMembresiaService;
import com.fitsystem.service.MembresiaService;
import com.fitsystem.service.RutinaService;
import com.fitsystem.service.UsuarioService;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
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
    private final MembresiaService membresiaService;
    private final MessageSource messageSource;

    public PerfilController(UsuarioService usuarioService, ClienteMembresiaService clienteMembresiaService,
            RutinaService rutinaService, MembresiaService membresiaService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.clienteMembresiaService = clienteMembresiaService;
        this.rutinaService = rutinaService;
        this.membresiaService = membresiaService;
        this.messageSource = messageSource;
    }

    // Dashboard "Inicio" del sidebar, con el contenido segun el rol del usuario seleccionado
    @GetMapping("/perfil/{idUsuario}")
    public String dashboard(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        Optional<com.fitsystem.domain.Usuario> usuarioOpt = cargarUsuarioOModel(idUsuario, model, redirectAttributes);
        if (usuarioOpt.isEmpty()) {
            return "redirect:/usuario/listado";
        }
        var usuario = usuarioOpt.get();

        switch (usuario.getRol()) {
            case CLIENTE -> {
                var suscripciones = clienteMembresiaService.getSuscripcionesDeCliente(idUsuario);
                model.addAttribute("suscripciones", suscripciones);
                var estados = new HashMap<Integer, String>();
                for (var s : suscripciones) {
                    estados.put(s.getIdClienteMembresia(), clienteMembresiaService.calcularEstado(s).name());
                }
                model.addAttribute("estados", estados);
                var rutinas = rutinaService.getRutinasDeCliente(idUsuario);
                model.addAttribute("rutinaActual", rutinas.isEmpty() ? null : rutinas.get(0));
            }
            case ENTRENADOR -> {
                model.addAttribute("clientesAsignados", rutinaService.getClientesDeEntrenador(idUsuario));
                model.addAttribute("totalRutinasAsignadas", rutinaService.getRutinasDeEntrenador(idUsuario).size());
            }
            case ADMINISTRADOR -> {
                model.addAttribute("totalClientes", usuarioService.getUsuariosActivosPorRol(RolUsuario.CLIENTE).size());
                model.addAttribute("totalEntrenadores", usuarioService.getUsuariosActivosPorRol(RolUsuario.ENTRENADOR).size());
                model.addAttribute("planes", membresiaService.getMembresias(true));
            }
        }
        return "/perfil/dashboard";
    }

    @GetMapping("/perfil/{idUsuario}/membresia")
    public String membresia(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        if (cargarUsuarioOModel(idUsuario, model, redirectAttributes).isEmpty()) {
            return "redirect:/usuario/listado";
        }
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
        if (cargarUsuarioOModel(idUsuario, model, redirectAttributes).isEmpty()) {
            return "redirect:/usuario/listado";
        }
        model.addAttribute("rutinas", rutinaService.getRutinasDeCliente(idUsuario));
        return "/perfil/rutinas";
    }

    @GetMapping("/perfil/{idUsuario}/clientes")
    public String clientes(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        if (cargarUsuarioOModel(idUsuario, model, redirectAttributes).isEmpty()) {
            return "redirect:/usuario/listado";
        }
        model.addAttribute("clientesAsignados", rutinaService.getClientesDeEntrenador(idUsuario));
        return "/perfil/clientes";
    }

    // Historia 12/13 vista desde el entrenador: rutinas que el mismo ha asignado
    @GetMapping("/perfil/{idUsuario}/misRutinas")
    public String misRutinas(@PathVariable Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        if (cargarUsuarioOModel(idUsuario, model, redirectAttributes).isEmpty()) {
            return "redirect:/usuario/listado";
        }
        model.addAttribute("rutinas", rutinaService.getRutinasDeEntrenador(idUsuario));
        return "/perfil/misRutinas";
    }

    private Optional<com.fitsystem.domain.Usuario> cargarUsuarioOModel(Integer idUsuario, Model model, RedirectAttributes redirectAttributes) {
        var usuarioOpt = usuarioService.getUsuario(idUsuario);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("usuario.error01", null, Locale.getDefault()));
            return usuarioOpt;
        }
        model.addAttribute("usuarioSeleccionado", usuarioOpt.get());
        return usuarioOpt;
    }
}
