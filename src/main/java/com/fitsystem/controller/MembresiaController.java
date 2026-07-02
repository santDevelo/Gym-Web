package com.fitsystem.controller;

import com.fitsystem.domain.Membresia;
import com.fitsystem.service.MembresiaService;
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
@RequestMapping("/membresia")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final MessageSource messageSource;

    public MembresiaController(MembresiaService membresiaService, MessageSource messageSource) {
        this.membresiaService = membresiaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var membresias = membresiaService.getMembresias(false);
        model.addAttribute("membresias", membresias);
        model.addAttribute("totalMembresias", membresias.size());
        return "/membresia/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Membresia membresia, RedirectAttributes redirectAttributes) {
        membresiaService.save(membresia);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/membresia/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idMembresia, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            membresiaService.delete(idMembresia);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "membresia.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "membresia.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "membresia.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/membresia/listado";
    }

    @GetMapping("/modificar/{idMembresia}")
    public String modificar(@PathVariable("idMembresia") Integer idMembresia, Model model, RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresiaOpt = membresiaService.getMembresia(idMembresia);
        if (membresiaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("membresia.error01", null, Locale.getDefault()));
            return "redirect:/membresia/listado";
        }
        model.addAttribute("membresia", membresiaOpt.get());
        return "/membresia/modifica";
    }
}
