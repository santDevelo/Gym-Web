package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.NombreRol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.RutinaService;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Gestiona las vistas y acciones de rutinas para clientes y entrenadores.
// La autorización por rol se comprueba antes de delegar cada operación al servicio.
@Controller
public class RutinaController {

    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private static final String REDIRECT_RUTINA_CLIENTE = "redirect:/cliente/rutina";
    private static final String REDIRECT_RUTINAS_ENTRENADOR = "redirect:/entrenador/rutinas";
    private static final List<String> DIAS_SEMANA = List.of(
            "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");

    private final UsuarioService usuarioService;
    private final RutinaService rutinaService;

    public RutinaController(UsuarioService usuarioService, RutinaService rutinaService) {
        this.usuarioService = usuarioService;
        this.rutinaService = rutinaService;
    }

    @GetMapping("/cliente/rutina")
    public String mostrarRutinaCliente(Principal principal, Model model) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        model.addAttribute("usuario", cliente);
        model.addAttribute(
                "rutina",
                rutinaService.buscarActivaPorCliente(cliente.getIdUsuario()).orElse(null));
        model.addAttribute("seccionActiva", "cliente-rutina");
        model.addAttribute("diasSemana", DIAS_SEMANA);
        return "cliente/rutina";
    }

    @GetMapping("/entrenador/rutinas")
    public String mostrarRutinasEntrenador(Principal principal, Model model) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        model.addAttribute("usuario", entrenador);
        model.addAttribute("rutinas", rutinaService.listarActivas());
        model.addAttribute("clientes", usuarioService.listarActivosPorRol(NombreRol.CLIENTE));
        model.addAttribute(
                "idsMembresiasActivas",
                obtenerIds(rutinaService.listarClientesConMembresiaActiva()));
        model.addAttribute(
                "idsClientesDisponibles",
                obtenerIds(rutinaService.listarClientesDisponibles()));
        model.addAttribute("fechaActual", LocalDate.now());
        model.addAttribute("diasSemana", DIAS_SEMANA);
        model.addAttribute("seccionActiva", "entrenador-rutinas");
        return "entrenador/rutinas";
    }

    @PostMapping("/entrenador/rutinas/agregar")
    public String agregarRutinaEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam String nombre,
            @RequestParam(required = false) String objetivo,
            @RequestParam(required = false) String descripcion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaAsignacion,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.crearRutina(
                        idCliente, nombre, objetivo, descripcion, fechaAsignacion),
                atributos,
                "La rutina fue creada correctamente.");
        return REDIRECT_RUTINAS_ENTRENADOR;
    }

    @PostMapping("/entrenador/rutinas/eliminar")
    public String eliminarRutinaEntrenador(
            @RequestParam Integer idRutina,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.eliminarRutina(idRutina),
                atributos,
                "La rutina fue eliminada correctamente.");
        return REDIRECT_RUTINAS_ENTRENADOR;
    }

    @PostMapping("/entrenador/rutinas/ejercicios/agregar")
    public String agregarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.agregarEjercicio(
                        idCliente, dia, nombre, series, repeticiones, observaciones),
                atributos,
                "El ejercicio fue agregado correctamente.");
        return REDIRECT_RUTINAS_ENTRENADOR;
    }

    @PostMapping("/entrenador/rutinas/ejercicios/editar")
    public String editarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam Integer idEjercicio,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.editarEjercicio(
                        idCliente,
                        idEjercicio,
                        dia,
                        nombre,
                        series,
                        repeticiones,
                        observaciones),
                atributos,
                "El ejercicio fue actualizado correctamente.");
        return REDIRECT_RUTINAS_ENTRENADOR;
    }

    @PostMapping("/entrenador/rutinas/ejercicios/eliminar")
    public String eliminarEjercicioEntrenador(
            @RequestParam Integer idCliente,
            @RequestParam Integer idEjercicio,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.eliminarEjercicio(idCliente, idEjercicio),
                atributos,
                "El ejercicio fue eliminado correctamente.");
        return REDIRECT_RUTINAS_ENTRENADOR;
    }

    @PostMapping("/cliente/rutina/ejercicios/agregar")
    public String agregarEjercicio(
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.agregarEjercicio(
                        cliente.getIdUsuario(), dia, nombre, series, repeticiones, observaciones),
                atributos,
                "El ejercicio fue agregado correctamente.");
        return REDIRECT_RUTINA_CLIENTE;
    }

    @PostMapping("/cliente/rutina/ejercicios/editar")
    public String editarEjercicio(
            @RequestParam Integer idEjercicio,
            @RequestParam String dia,
            @RequestParam String nombre,
            @RequestParam Integer series,
            @RequestParam String repeticiones,
            @RequestParam(required = false) String observaciones,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.editarEjercicio(
                        cliente.getIdUsuario(),
                        idEjercicio,
                        dia,
                        nombre,
                        series,
                        repeticiones,
                        observaciones),
                atributos,
                "El ejercicio fue actualizado correctamente.");
        return REDIRECT_RUTINA_CLIENTE;
    }

    @PostMapping("/cliente/rutina/ejercicios/eliminar")
    public String eliminarEjercicio(
            @RequestParam Integer idEjercicio,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> rutinaService.eliminarEjercicio(cliente.getIdUsuario(), idEjercicio),
                atributos,
                "El ejercicio fue eliminado correctamente.");
        return REDIRECT_RUTINA_CLIENTE;
    }

    // Convierte la lista de clientes en los identificadores que utiliza la vista.
    private List<Integer> obtenerIds(List<Usuario> usuarios) {
        return usuarios.stream().map(Usuario::getIdUsuario).toList();
    }

    private Usuario obtenerUsuarioAutenticado(Principal principal) {
        return usuarioService.buscarPorUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario autenticado no existe."));
    }

    // Centraliza la creación de mensajes temporales después de una operación.
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
