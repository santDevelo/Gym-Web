package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.RutinaService;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Controlador de Rutina y EjercicioRutina. Atiende tanto las rutas del
// cliente ("/cliente/rutina/...", solo su propia rutina) como las del
// entrenador ("/entrenador/rutinas/...", cualquier cliente), reutilizando el
// mismo RutinaService en vez de duplicar controlador por rol.
@Controller
public class RutinaController {

    private static final List<String> DIAS_SEMANA
            = List.of(
                    "Lunes",
                    "Martes",
                    "Miércoles",
                    "Jueves",
                    "Viernes",
                    "Sábado",
                    "Domingo");

    private final UsuarioService usuarioService;
    private final RutinaService rutinaService;

    public RutinaController(
            UsuarioService usuarioService,
            RutinaService rutinaService) {

        this.usuarioService = usuarioService;
        this.rutinaService = rutinaService;
    }

    @GetMapping("/cliente/rutina")
    public String mostrarRutinaCliente(
            Principal principal,
            Model model) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "rutina",
                rutinaService
                        .buscarActivaPorCliente(
                                usuario.getIdUsuario())
                        .orElse(null));
        model.addAttribute(
                "seccionActiva",
                "cliente-rutina");
        model.addAttribute(
                "diasSemana",
                DIAS_SEMANA);

        return "cliente/rutina";
    }

    @GetMapping("/entrenador/rutinas")
    public String mostrarRutinasEntrenador(
            Principal principal,
            Model model) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", entrenador);
        model.addAttribute(
                "rutinas",
                rutinaService.listarActivas());
        model.addAttribute(
                "clientes",
                usuarioService.listarActivosPorRol(
                        "CLIENTE"));
        model.addAttribute(
                "idsMembresiasActivas",
                rutinaService
                        .listarClientesConMembresiaActiva()
                        .stream()
                        .map(Usuario::getIdUsuario)
                        .toList());
        model.addAttribute(
                "idsClientesDisponibles",
                rutinaService
                        .listarClientesDisponibles()
                        .stream()
                        .map(Usuario::getIdUsuario)
                        .toList());
        model.addAttribute(
                "fechaActual",
                LocalDate.now());
        model.addAttribute(
                "diasSemana",
                DIAS_SEMANA);
        model.addAttribute(
                "seccionActiva",
                "entrenador-rutinas");

        return "entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/agregar")
    public String agregarRutinaEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam String nombre,
            @RequestParam(required = false)
            String objetivo,
            @RequestParam(required = false)
            String descripcion,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaAsignacion,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.crearRutina(
                    idCliente,
                    nombre,
                    objetivo,
                    descripcion,
                    fechaAsignacion);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "La rutina fue creada correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/eliminar")
    public String eliminarRutinaEntrenador(
            @RequestParam Integer idRutina,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.eliminarRutina(idRutina);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "La rutina fue eliminada correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/ejercicios/agregar")
    public String agregarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false)
            String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.agregarEjercicio(
                    idCliente,
                    dia,
                    nombre,
                    series,
                    repeticiones,
                    observaciones);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue agregado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/ejercicios/editar")
    public String editarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam Integer idEjercicio,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false)
            String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.editarEjercicio(
                    idCliente,
                    idEjercicio,
                    dia,
                    nombre,
                    series,
                    repeticiones,
                    observaciones);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue actualizado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/ejercicios/eliminar")
    public String eliminarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam Integer idEjercicio,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.eliminarEjercicio(
                    idCliente,
                    idEjercicio);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue eliminado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/entrenador/rutinas";
    }

    @PostMapping("/cliente/rutina/ejercicios/agregar")
    public String agregarEjercicio(
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false)
            String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.agregarEjercicio(
                    usuario.getIdUsuario(),
                    dia,
                    nombre,
                    series,
                    repeticiones,
                    observaciones);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue agregado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/rutina";
    }

    @PostMapping("/cliente/rutina/ejercicios/editar")
    public String editarEjercicio(
            @RequestParam Integer idEjercicio,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false)
            String observaciones,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.editarEjercicio(
                    usuario.getIdUsuario(),
                    idEjercicio,
                    dia,
                    nombre,
                    series,
                    repeticiones,
                    observaciones);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue actualizado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/rutina";
    }

    @PostMapping("/cliente/rutina/ejercicios/eliminar")
    public String eliminarEjercicio(
            @RequestParam Integer idEjercicio,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            rutinaService.eliminarEjercicio(
                    usuario.getIdUsuario(),
                    idEjercicio);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El ejercicio fue eliminado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/rutina";
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
