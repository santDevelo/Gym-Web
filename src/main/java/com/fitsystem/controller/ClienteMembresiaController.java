package com.fitsystem.controller;

import com.fitsystem.domain.ClienteMembresia;
import com.fitsystem.domain.RolUsuario;
import com.fitsystem.service.ClienteMembresiaService;
import com.fitsystem.service.MembresiaService;
import com.fitsystem.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/suscripcion")
public class ClienteMembresiaController {

    private final ClienteMembresiaService clienteMembresiaService;
    private final UsuarioService usuarioService;
    private final MembresiaService membresiaService;
    private final MessageSource messageSource;

    public ClienteMembresiaController(ClienteMembresiaService clienteMembresiaService, UsuarioService usuarioService,
            MembresiaService membresiaService, MessageSource messageSource) {
        this.clienteMembresiaService = clienteMembresiaService;
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var suscripciones = clienteMembresiaService.getSuscripciones();
        model.addAttribute("suscripciones", suscripciones);
        model.addAttribute("totalSuscripciones", suscripciones.size());
        var estados = new HashMap<Integer, String>();
        for (var s : suscripciones) {
            estados.put(s.getIdClienteMembresia(), clienteMembresiaService.calcularEstado(s).name());
        }
        model.addAttribute("estados", estados);
        model.addAttribute("clientes", usuarioService.getUsuariosActivosPorRol(RolUsuario.CLIENTE));
        model.addAttribute("planes", membresiaService.getMembresias(true));
        return "/clienteMembresia/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid ClienteMembresia clienteMembresia, RedirectAttributes redirectAttributes) {
        clienteMembresiaService.save(clienteMembresia);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/suscripcion/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idClienteMembresia, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            clienteMembresiaService.delete(idClienteMembresia);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "suscripcion.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "suscripcion.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "suscripcion.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/suscripcion/listado";
    }

    @GetMapping("/modificar/{idClienteMembresia}")
    public String modificar(@PathVariable("idClienteMembresia") Integer idClienteMembresia, Model model,
            RedirectAttributes redirectAttributes) {
        Optional<ClienteMembresia> suscripcionOpt = clienteMembresiaService.getSuscripcion(idClienteMembresia);
        if (suscripcionOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("suscripcion.error01", null, Locale.getDefault()));
            return "redirect:/suscripcion/listado";
        }
        model.addAttribute("suscripcion", suscripcionOpt.get());
        model.addAttribute("clientes", usuarioService.getUsuariosActivosPorRol(RolUsuario.CLIENTE));
        model.addAttribute("planes", membresiaService.getMembresias(true));
        return "/clienteMembresia/modifica";
    }
}
