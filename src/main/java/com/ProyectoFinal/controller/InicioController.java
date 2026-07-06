package com.ProyectoFinal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/cliente")
    public String cliente() {
        return "cliente/inicio";
    }

    @GetMapping("/entrenador")
    public String entrenador() {
        return "entrenador/inicio";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/inicio";
    }

}