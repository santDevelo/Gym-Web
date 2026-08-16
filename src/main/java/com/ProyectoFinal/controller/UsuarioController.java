package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.NombreRol;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.MembresiaService;
import com.ProyectoFinal.service.PlanMembresiaService;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Gestiona el listado, creación y modificación de usuarios desde administración.
// La edición de la membresía se habilita únicamente cuando el usuario es cliente.
@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private static final String REDIRECT_LISTADO = "redirect:/usuario/listado";

    private final UsuarioService usuarioService;
    private final MembresiaService membresiaService;
    private final PlanMembresiaService planMembresiaService;

    public UsuarioController(
            UsuarioService usuarioService,
            MembresiaService membresiaService,
            PlanMembresiaService planMembresiaService) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
        this.planMembresiaService = planMembresiaService;
    }

    @GetMapping("/listado")
    public String listado(
            @RequestParam(name = "rol", required = false) String filtroRol,
            Principal principal,
            Model model) {
        cargarDatosComunes(principal, model, determinarSeccionActiva(filtroRol));

        List<Usuario> usuarios = obtenerUsuarios(filtroRol);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("usuarioFormulario", new Usuario());
        return "admin/listado";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("usuarioFormulario") Usuario formulario,
            @RequestParam Integer idRol,
            @RequestParam(name = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes atributos) {
        ejecutarAccion(
                () -> usuarioService.guardarDesdeAdministracion(formulario, idRol, imagenFile),
                atributos,
                "El usuario fue guardado correctamente.");
        return REDIRECT_LISTADO;
    }

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(
            @PathVariable Integer idUsuario,
            Principal principal,
            Model model,
            RedirectAttributes atributos) {
        Usuario formulario = usuarioService.buscarPorId(idUsuario).orElse(null);
        if (formulario == null) {
            atributos.addFlashAttribute("error", "El usuario no fue encontrado.");
            return REDIRECT_LISTADO;
        }

        cargarDatosComunes(principal, model, "usuarios");
        model.addAttribute("usuarioFormulario", formulario);
        model.addAttribute("idRolActual", obtenerIdRolActual(formulario));

        if (formulario.tieneRol(NombreRol.CLIENTE)) {
            cargarDatosMembresia(model, formulario);
        }
        return "admin/modifica";
    }

    @PostMapping("/membresia/guardar")
    public String guardarMembresia(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idPlan,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaVencimiento,
            @RequestParam EstadoMembresia estado,
            RedirectAttributes atributos) {
        try {
            membresiaService.guardarMembresiaCliente(
                    idUsuario, idPlan, fechaInicio, fechaVencimiento, estado);
            atributos.addFlashAttribute(
                    "todoOk", "La membresía del cliente fue guardada correctamente.");
            return REDIRECT_LISTADO + "?rol=" + NombreRol.CLIENTE.name();
        } catch (IllegalArgumentException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
            return "redirect:/usuario/modificar/" + idUsuario;
        }
    }

    @PostMapping("/cambiar-estado/{idUsuario}")
    public String cambiarEstado(
            @PathVariable Integer idUsuario,
            Principal principal,
            RedirectAttributes atributos) {
        ejecutarAccion(
                () -> usuarioService.cambiarEstado(idUsuario, principal.getName()),
                atributos,
                "El estado del usuario fue actualizado.");
        return REDIRECT_LISTADO;
    }

    // Agrega al modelo los datos compartidos por el listado y el formulario de modificación.
    private void cargarDatosComunes(
            Principal principal,
            Model model,
            String seccionActiva) {
        Usuario usuarioAutenticado = usuarioService.buscarPorUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el usuario autenticado."));

        model.addAttribute("usuario", usuarioAutenticado);
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("seccionActiva", seccionActiva);
    }

    // Prepara el formulario de membresía existente o uno nuevo con valores iniciales.
    private void cargarDatosMembresia(Model model, Usuario cliente) {
        Membresia membresia = membresiaService
                .buscarUltimaPorUsuario(cliente.getIdUsuario())
                .orElseGet(() -> crearMembresiaInicial(cliente));

        model.addAttribute("membresiaFormulario", membresia);
        model.addAttribute("planes", planMembresiaService.listarActivos());
        model.addAttribute("estadosMembresia", EstadoMembresia.values());
    }

    private Membresia crearMembresiaInicial(Usuario cliente) {
        LocalDate fechaInicio = LocalDate.now();
        Membresia membresia = new Membresia();
        membresia.setUsuario(cliente);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaInicio.plusMonths(1));
        membresia.setEstado(EstadoMembresia.ACTIVA);
        return membresia;
    }

    private Integer obtenerIdRolActual(Usuario usuario) {
        return usuario.getRoles().stream()
                .findFirst()
                .map(Rol::getIdRol)
                .orElse(null);
    }

    // Aplica el filtro recibido en la URL sin duplicar consultas en el controlador.
    private List<Usuario> obtenerUsuarios(String filtroRol) {
        if (NombreRol.CLIENTE.coincideCon(filtroRol)) {
            return usuarioService.listarPorRol(NombreRol.CLIENTE);
        }
        if (NombreRol.ENTRENADOR.coincideCon(filtroRol)) {
            return usuarioService.listarPorRol(NombreRol.ENTRENADOR);
        }
        return usuarioService.listarTodosConRoles();
    }

    private String determinarSeccionActiva(String filtroRol) {
        if (NombreRol.CLIENTE.coincideCon(filtroRol)) {
            return "clientes";
        }
        if (NombreRol.ENTRENADOR.coincideCon(filtroRol)) {
            return "empleados";
        }
        return "usuarios";
    }

    // Ejecuta una acción y prepara el mensaje que se mostrará después de la redirección.
    private void ejecutarAccion(
            Runnable accion,
            RedirectAttributes atributos,
            String mensajeExito) {
        try {
            accion.run();
            atributos.addFlashAttribute("todoOk", mensajeExito);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            atributos.addFlashAttribute("error", ex.getMessage());
        }
    }
}
