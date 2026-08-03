package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.MembresiaService;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final MembresiaService membresiaService;

    public DashboardController(
            UsuarioService usuarioService,
            MembresiaService membresiaService
    ) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
    }

    @GetMapping({
        "/dashboard",
        "/dashboard/"
    })
    public String mostrarDashboard(
            Principal principal,
            Model model
    ) {

        Usuario usuario = usuarioService
                .getUsuarioPorUsername(
                        principal.getName()
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "El usuario autenticado no existe."
                        )
                );

        model.addAttribute("usuario", usuario);

        if (usuario.tieneRol("ADMINISTRADOR")) {

            model.addAttribute(
                    "clientesActivos",
                    usuarioService.contarPorRolActivo(
                            "CLIENTE"
                    )
            );

            model.addAttribute(
                    "entrenadoresActivos",
                    usuarioService.contarPorRolActivo(
                            "ENTRENADOR"
                    )
            );

            model.addAttribute(
                    "ingresosMes",
                    membresiaService.ingresosDelMes()
            );

            model.addAttribute(
                    "pagosPendientes",
                    membresiaService.contarPorEstado(
                            EstadoMembresia.PENDIENTE
                    )
            );
        }

        if (usuario.tieneRol("ENTRENADOR")) {

            model.addAttribute(
                    "clientesAsignados",
                    usuarioService.contarPorRolActivo(
                            "CLIENTE"
                    )
            );
        }

        if (usuario.tieneRol("CLIENTE")) {

            model.addAttribute(
                    "membresia",
                    membresiaService
                            .buscarUltimaPorUsuario(
                                    usuario.getIdUsuario()
                            )
                            .orElse(null)
            );
        }

        return "dashboard/listado";
    }
}