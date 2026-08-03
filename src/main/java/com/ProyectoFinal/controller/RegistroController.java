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

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(
            RegistroService registroService
    ) {
        this.registroService = registroService;
    }

    @GetMapping
    public String nuevo(
            Usuario usuario
    ) {
        return "auth/registro";
    }

    @PostMapping
    public String crearUsuario(
            @Valid Usuario usuario,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Verifique los datos ingresados."
            );

            return "redirect:/registro";
        }

        boolean creado =
                registroService.crearUsuario(usuario);

        if (!creado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario o el correo ya existen."
            );

            return "redirect:/registro";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cuenta creada correctamente."
        );

        return "redirect:/login";
    }
}