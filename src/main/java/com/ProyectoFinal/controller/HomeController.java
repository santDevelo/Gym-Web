package com.ProyectoFinal.controller;

import com.ProyectoFinal.service.HomeService;
import com.ProyectoFinal.domain.Home;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Controlador de "/home", equivalente público a "/" (ver ruta pública en la
// tabla "ruta"). Reutiliza la misma vista "index".
@Controller
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

  @GetMapping("/home")
public String mostrarHome(Model model) {

    Home home = homeService.getHomePrincipal();

    System.out.println("URL encontrada: " + home.getImagenUrl());

    model.addAttribute("imagenGimnasio", home.getImagenUrl());

    return "index";
}
}
