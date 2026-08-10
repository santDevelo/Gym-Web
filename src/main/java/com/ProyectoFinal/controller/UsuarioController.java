package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.UsuarioService;
import java.security.Principal;
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

    public UsuarioController(
            UsuarioService usuarioService) {

        this.usuarioService = usuarioService;
    }

    /*
     * Mostrar listado
     */

    @GetMapping("/listado")
    public String listado(
            Principal principal,
            Model model) {

        cargarDatosComunes(
                principal,
                model);

        var usuarios
                = usuarioService.listarTodosConRoles();

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
                model);

        model.addAttribute(
                "usuarioFormulario",
                usuarioFormulario);

        model.addAttribute(
                "idRolActual",
                idRolActual);

        return "usuario/modifica";
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
            Model model) {

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
                "usuarios");
    }
}