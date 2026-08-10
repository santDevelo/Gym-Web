package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
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

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

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

    /*
     * Mostrar listado
     */

    @GetMapping("/listado")
    public String listado(
            @RequestParam(
                    name = "rol",
                    required = false)
            String filtroRol,
            Principal principal,
            Model model) {

        String seccionActiva
                = determinarSeccionActiva(filtroRol);

        cargarDatosComunes(
                principal,
                model,
                seccionActiva);

        List<Usuario> usuarios
                = obtenerUsuarios(filtroRol);

        model.addAttribute(
                "usuarios",
                usuarios);

        model.addAttribute(
                "totalUsuarios",
                usuarios.size());

        model.addAttribute(
                "usuarioFormulario",
                new Usuario());

        return "usuario/listado";
    }

    /*
     * Guardar usuario nuevo o modificado
     */

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("usuarioFormulario")
            Usuario usuarioFormulario,
            @RequestParam("idRol")
            Integer idRol,
            @RequestParam(
                    name = "imagenFile",
                    required = false)
            MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        try {

            usuarioService.guardarDesdeAdministracion(
                    usuarioFormulario,
                    idRol,
                    imagenFile);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El usuario fue guardado correctamente.");

        } catch (IllegalArgumentException
                | IllegalStateException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/usuario/listado";
    }

    /*
     * Mostrar formulario de modificación
     */

    @GetMapping("/modificar/{idUsuario}")
    public String modificar(
            @PathVariable Integer idUsuario,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        var usuarioOptional
                = usuarioService.buscarPorId(
                        idUsuario);

        if (usuarioOptional.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario no fue encontrado.");

            return "redirect:/usuario/listado";
        }

        Usuario usuarioFormulario
                = usuarioOptional.get();

        Integer idRolActual
                = usuarioFormulario
                        .getRoles()
                        .stream()
                        .findFirst()
                        .map(Rol::getIdRol)
                        .orElse(null);

        cargarDatosComunes(
                principal,
                model,
                "usuarios");

        model.addAttribute(
                "usuarioFormulario",
                usuarioFormulario);

        model.addAttribute(
                "idRolActual",
                idRolActual);

        if (usuarioFormulario.tieneRol("CLIENTE")) {

            Membresia membresiaFormulario
                    = membresiaService
                            .buscarUltimaPorUsuario(idUsuario)
                            .orElseGet(() ->
                            crearMembresiaInicial(usuarioFormulario));

            model.addAttribute(
                    "membresiaFormulario",
                    membresiaFormulario);

            model.addAttribute(
                    "planes",
                    planMembresiaService.listarActivos());

            model.addAttribute(
                    "estadosMembresia",
                    EstadoMembresia.values());
        }

        return "usuario/modifica";
    }

    /*
     * Guardar o modificar la membresía de un cliente.
     */
    @PostMapping("/membresia/guardar")
    public String guardarMembresia(
            @RequestParam Integer idUsuario,
            @RequestParam Integer idPlan,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaVencimiento,
            @RequestParam EstadoMembresia estado,
            RedirectAttributes redirectAttributes) {

        try {

            membresiaService.guardarMembresiaCliente(
                    idUsuario,
                    idPlan,
                    fechaInicio,
                    fechaVencimiento,
                    estado);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "La membresía del cliente fue guardada correctamente.");

            return "redirect:/usuario/listado?rol=CLIENTE";

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());

            return "redirect:/usuario/modificar/"
                    + idUsuario;
        }
    }

    /*
     * Activar o desactivar
     */

    @PostMapping(
            "/cambiar-estado/{idUsuario}")
    public String cambiarEstado(
            @PathVariable Integer idUsuario,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {

            usuarioService.cambiarEstado(
                    idUsuario,
                    principal.getName());

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El estado del usuario fue actualizado.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/usuario/listado";
    }

    /*
     * Datos compartidos por las vistas
     */

    private void cargarDatosComunes(
            Principal principal,
            Model model,
            String seccionActiva) {

        Usuario usuarioAutenticado
                = usuarioService
                        .buscarPorUsername(
                                principal.getName())
                        .orElseThrow(() ->
                        new IllegalStateException(
                                "No se encontró el usuario autenticado."));

        model.addAttribute(
                "usuario",
                usuarioAutenticado);

        model.addAttribute(
                "roles",
                usuarioService.listarRoles());

        model.addAttribute(
                "seccionActiva",
                seccionActiva);
    }

    private Membresia crearMembresiaInicial(
            Usuario cliente) {

        LocalDate fechaInicio = LocalDate.now();

        Membresia membresia = new Membresia();

        membresia.setUsuario(cliente);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(
                fechaInicio.plusMonths(1));
        membresia.setEstado(
                EstadoMembresia.ACTIVA);

        return membresia;
    }

    private List<Usuario> obtenerUsuarios(
            String filtroRol) {

        if ("CLIENTE".equalsIgnoreCase(
                filtroRol)) {

            return usuarioService.listarPorRol(
                    "CLIENTE");
        }

        if ("ENTRENADOR".equalsIgnoreCase(
                filtroRol)) {

            return usuarioService.listarPorRol(
                    "ENTRENADOR");
        }

        return usuarioService.listarTodosConRoles();
    }

    private String determinarSeccionActiva(
            String filtroRol) {

        if ("CLIENTE".equalsIgnoreCase(
                filtroRol)) {

            return "clientes";
        }

        if ("ENTRENADOR".equalsIgnoreCase(
                filtroRol)) {

            return "empleados";
        }

        return "usuarios";
    }
}
