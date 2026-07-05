package com.fitsystem.controller;

import com.fitsystem.domain.RolUsuario;
import com.fitsystem.service.MembresiaService;
import com.fitsystem.service.UsuarioService;
import java.util.Comparator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    // Las ultimas versiones de Spring recomiendan utilizar final y constructor en lugar de @Autowired
    private final UsuarioService usuarioService;
    private final MembresiaService membresiaService;

    public IndexController(UsuarioService usuarioService, MembresiaService membresiaService) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
    }

    @GetMapping("/")
    public String cargarPaginaInicio(Model model) {
        model.addAttribute("totalClientes", usuarioService.getUsuariosActivosPorRol(RolUsuario.CLIENTE).size());
        model.addAttribute("totalEntrenadores", usuarioService.getUsuariosActivosPorRol(RolUsuario.ENTRENADOR).size());
        var planes = membresiaService.getMembresias(true);
        planes.sort(Comparator.comparing(com.fitsystem.domain.Membresia::getPrecio).reversed());
        model.addAttribute("planes", planes);
        return "/index";
    }
}
