package com.fitsystem.controller;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.domain.Rutina;
import com.fitsystem.service.RutinaService;
import com.fitsystem.service.UsuarioService;
import jakarta.validation.Valid;
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
@RequestMapping("/rutina")
public class RutinaController {

    private final RutinaService rutinaService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public RutinaController(RutinaService rutinaService, UsuarioService usuarioService, MessageSource messageSource) {
        this.rutinaService = rutinaService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var rutinas = rutinaService.getRutinas();
        model.addAttribute("rutinas", rutinas);
        model.addAttribute("totalRutinas", rutinas.size());
        cargarSelects(model);
        return "/rutina/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Rutina rutina, RedirectAttributes redirectAttributes) {
        rutinaService.save(rutina);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/rutina/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idRutina, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            rutinaService.delete(idRutina);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "rutina.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "rutina.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "rutina.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/rutina/listado";
    }

    @GetMapping("/modificar/{idRutina}")
    public String modificar(@PathVariable("idRutina") Integer idRutina, Model model, RedirectAttributes redirectAttributes) {
        Optional<Rutina> rutinaOpt = rutinaService.getRutina(idRutina);
        if (rutinaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("rutina.error01", null, Locale.getDefault()));
            return "redirect:/rutina/listado";
        }
        model.addAttribute("rutina", rutinaOpt.get());
        cargarSelects(model);
        return "/rutina/modifica";
    }

    private void cargarSelects(Model model) {
        model.addAttribute("entrenadores", usuarioService.getUsuariosActivosPorRol(RolUsuario.ENTRENADOR));
        model.addAttribute("clientes", usuarioService.getUsuariosActivosPorRol(RolUsuario.CLIENTE));
    }
}
