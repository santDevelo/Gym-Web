package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Gestiona el formulario público utilizado para registrar nuevos clientes.
@Controller
@RequestMapping("/registro")
public class RegistroController {

    private static final String REDIRECT_REGISTRO = "redirect:/registro";

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping
    public String nuevo(Usuario usuario) {
        return "auth/registro";
    }

    @PostMapping
    public String crearUsuario(
            @Valid Usuario usuario,
            BindingResult resultadoValidacion,
            RedirectAttributes atributos) {
        if (resultadoValidacion.hasErrors()) {
            atributos.addFlashAttribute("error", "Verifique los datos ingresados.");
            return REDIRECT_REGISTRO;
        }

        if (!registroService.crearUsuario(usuario)) {
            atributos.addFlashAttribute("error", "El usuario o el correo ya existen.");
            return REDIRECT_REGISTRO;
        }

        atributos.addFlashAttribute("mensaje", "Cuenta creada correctamente.");
        return "redirect:/login";
    }
}
