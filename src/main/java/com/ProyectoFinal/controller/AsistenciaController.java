package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.NombreRol;
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

    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private static final String REDIRECT_ASISTENCIA = "redirect:/cliente/asistencia";

    private final UsuarioService usuarioService;
    private final AsistenciaService asistenciaService;

    public AsistenciaController(
            UsuarioService usuarioService,
            AsistenciaService asistenciaService) {
        this.usuarioService = usuarioService;
        this.asistenciaService = asistenciaService;
    }

    @GetMapping("/cliente/asistencia")
    public String mostrarAsistenciaCliente(Principal principal, Model model) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        Integer idCliente = cliente.getIdUsuario();
        model.addAttribute("usuario", cliente);
        model.addAttribute("asistencias", asistenciaService.listarPorCliente(idCliente));
        model.addAttribute(
                "totalAsistenciasMes", asistenciaService.contarAsistenciasDelMes(idCliente));
        model.addAttribute("seccionActiva", "cliente-asistencia");
        model.addAttribute("fechaActual", LocalDate.now());
        return "cliente/asistencia";
    }

    @PostMapping("/cliente/asistencia/agregar")
    public String agregarAsistencia(
            @RequestParam LocalDate fecha,
            @RequestParam LocalTime horaEntrada,
            @RequestParam(required = false) LocalTime horaSalida,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> asistenciaService.agregar(
                        cliente.getIdUsuario(), fecha, horaEntrada, horaSalida),
                atributos,
                "La asistencia fue agregada correctamente.");
        return REDIRECT_ASISTENCIA;
    }

    @PostMapping("/cliente/asistencia/eliminar")
    public String eliminarAsistencia(
            @RequestParam Integer idAsistencia,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> asistenciaService.eliminar(cliente.getIdUsuario(), idAsistencia),
                atributos,
                "La asistencia fue eliminada correctamente.");
        return REDIRECT_ASISTENCIA;
    }

    private Usuario obtenerUsuarioAutenticado(Principal principal) {
        return usuarioService.buscarPorUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario autenticado no existe."));
    }

    private void ejecutarAccion(
            Runnable accion,
            RedirectAttributes atributos,
            String mensajeExito) {
        try {
            accion.run();
            atributos.addFlashAttribute("todoOk", mensajeExito);
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
        }
    }
}
