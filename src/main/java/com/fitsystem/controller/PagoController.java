package com.fitsystem.controller;

import com.fitsystem.domain.Pago;
import com.fitsystem.service.ClienteMembresiaService;
import com.fitsystem.service.PagoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
@RequestMapping("/pago")
public class PagoController {

    private final PagoService pagoService;
    private final ClienteMembresiaService clienteMembresiaService;
    private final MessageSource messageSource;

    public PagoController(PagoService pagoService, ClienteMembresiaService clienteMembresiaService, MessageSource messageSource) {
        this.pagoService = pagoService;
        this.clienteMembresiaService = clienteMembresiaService;
        this.messageSource = messageSource;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var pagos = pagoService.getPagos();
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagos", pagos.size());
        model.addAttribute("suscripciones", clienteMembresiaService.getSuscripciones());
        return "/pago/listado";
    }

    // Historia 7, siguiendo el trio de consultas de la semana 8 (derivada, JPQL, SQL) sobre fechaPago
    @PostMapping("/consultaDerivada")
    public String consultaDerivada(Model model, @RequestParam LocalDate fechaInf, @RequestParam LocalDate fechaSup) {
        cargarConsulta(model, pagoService.consultaDerivada(fechaInf, fechaSup), fechaInf, fechaSup);
        return "/pago/listado";
    }

    @PostMapping("/consultaJPQL")
    public String consultaJPQL(Model model, @RequestParam LocalDate fechaInf, @RequestParam LocalDate fechaSup) {
        cargarConsulta(model, pagoService.consultaJPQL(fechaInf, fechaSup), fechaInf, fechaSup);
        return "/pago/listado";
    }

    @PostMapping("/consultaSQL")
    public String consultaSQL(Model model, @RequestParam LocalDate fechaInf, @RequestParam LocalDate fechaSup) {
        cargarConsulta(model, pagoService.consultaSQL(fechaInf, fechaSup), fechaInf, fechaSup);
        return "/pago/listado";
    }

    private void cargarConsulta(Model model, java.util.List<Pago> pagos, LocalDate fechaInf, LocalDate fechaSup) {
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagos", pagos.size());
        model.addAttribute("fechaInf", fechaInf);
        model.addAttribute("fechaSup", fechaSup);
        model.addAttribute("suscripciones", clienteMembresiaService.getSuscripciones());
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Pago pago, RedirectAttributes redirectAttributes) {
        pagoService.save(pago);
        redirectAttributes.addFlashAttribute("todoOk", messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        return "redirect:/pago/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPago, RedirectAttributes redirectAttributes) {
        String titulo = "todoOk";
        String detalle = "mensaje.eliminado";
        try {
            pagoService.delete(idPago);
        } catch (IllegalArgumentException e) {
            titulo = "error";
            detalle = "pago.error01";
        } catch (IllegalStateException e) {
            titulo = "error";
            detalle = "pago.error02";
        } catch (Exception e) {
            titulo = "error";
            detalle = "pago.error03";
        }
        redirectAttributes.addFlashAttribute(titulo, messageSource.getMessage(detalle, null, Locale.getDefault()));
        return "redirect:/pago/listado";
    }

    @GetMapping("/modificar/{idPago}")
    public String modificar(@PathVariable("idPago") Integer idPago, Model model, RedirectAttributes redirectAttributes) {
        Optional<Pago> pagoOpt = pagoService.getPago(idPago);
        if (pagoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", messageSource.getMessage("pago.error01", null, Locale.getDefault()));
            return "redirect:/pago/listado";
        }
        model.addAttribute("pago", pagoOpt.get());
        model.addAttribute("suscripciones", clienteMembresiaService.getSuscripciones());
        return "/pago/modifica";
    }
}
