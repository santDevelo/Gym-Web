package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Home;
import com.ProyectoFinal.service.HomeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final HomeService homeService;

    public IndexController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/")
    public String index(Model model) {

        Home home = homeService.getHomePrincipal();

        model.addAttribute("imagenGimnasio", home.getImagenUrl());

        return "index";
    }
}