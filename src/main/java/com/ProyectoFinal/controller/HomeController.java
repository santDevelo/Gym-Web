package com.ProyectoFinal.controller;

import com.ProyectoFinal.service.HomeService;
import com.ProyectoFinal.domain.Home;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// Muestra la página principal cuando se accede mediante la ruta alternativa /home.
@Controller
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public String mostrarHome(Model model) {
        Home home = homeService.obtenerHomePrincipal();
        model.addAttribute("imagenGimnasio", home.getImagenUrl());
        return "index";
    }
}
