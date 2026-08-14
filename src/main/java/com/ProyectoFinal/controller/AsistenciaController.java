package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.AsistenciaService;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AsistenciaController {

    private final UsuarioService usuarioService;
    private final AsistenciaService asistenciaService;

    public AsistenciaController(
            UsuarioService usuarioService,
            AsistenciaService asistenciaService) {

        this.usuarioService = usuarioService;
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/cliente/asistencia")
    public String mostrarAsistenciaCliente(
            Principal principal,
            Model model) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "asistencias",
                asistenciaService.listarPorCliente(
                        usuario.getIdUsuario()));
        model.addAttribute(
                "totalAsistenciasMes",
                asistenciaService.contarAsistenciasDelMes(
                        usuario.getIdUsuario()));
        model.addAttribute(
                "seccionActiva",
                "cliente-asistencia");
        model.addAttribute("fechaActual", LocalDate.now());

        return "cliente/asistencia";
    }

    @PostMapping("/cliente/asistencia/agregar")
    public String agregarAsistencia(
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime horaEntrada,
            @RequestParam(required = false)
            LocalTime horaSalida,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            asistenciaService.agregar(
                    usuario.getIdUsuario(),
                    fecha,
                    horaEntrada,
                    horaSalida);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "La asistencia fue agregada correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/asistencia";
    }

    @PostMapping("/cliente/asistencia/eliminar")
    public String eliminarAsistencia(
            @RequestParam Integer idAsistencia,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            asistenciaService.eliminar(
                    usuario.getIdUsuario(),
                    idAsistencia);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "La asistencia fue eliminada correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/asistencia";
    }

    private Usuario obtenerUsuarioAutenticado(
            Principal principal) {

        return usuarioService
                .buscarPorUsername(principal.getName())
                .orElseThrow(() ->
                new IllegalStateException(
                        "El usuario autenticado no existe."));
    }
}
