package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.MetodoPago;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.MembresiaService;
import com.ProyectoFinal.service.PagoService;
import com.ProyectoFinal.service.PlanMembresiaService;
import com.ProyectoFinal.service.UsuarioService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DashboardController {

    private final UsuarioService usuarioService;
    private final MembresiaService membresiaService;
    private final PlanMembresiaService planMembresiaService;
    private final PagoService pagoService;

    public DashboardController(
            UsuarioService usuarioService,
            MembresiaService membresiaService,
            PlanMembresiaService planMembresiaService,
            PagoService pagoService
    ) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
        this.planMembresiaService = planMembresiaService;
        this.pagoService = pagoService;
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
                .buscarPorUsername(
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

    @GetMapping("/admin/membresias")
    public String mostrarMembresias(
            Principal principal,
            Model model) {

        Usuario usuario = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("ADMINISTRADOR")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "planes",
                planMembresiaService.listarTodos());
        model.addAttribute(
                "seccionActiva",
                "membresias");

        return "admin/membresias";
    }

    @PostMapping("/admin/membresias/guardar")
    public String guardarPlanMembresia(
            @RequestParam Integer idPlan,
            @RequestParam String nombre,
            @RequestParam(
                    name = "descripcion",
                    required = false)
            String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam(
                    name = "activo",
                    defaultValue = "false")
            boolean activo,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("ADMINISTRADOR")) {
            return "redirect:/dashboard";
        }

        try {

            planMembresiaService.actualizar(
                    idPlan,
                    nombre,
                    descripcion,
                    precio,
                    activo);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El plan fue actualizado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/admin/membresias";
    }

    @GetMapping("/admin/pagos")
    public String mostrarPagos(
            Principal principal,
            Model model) {

        Usuario usuario = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("ADMINISTRADOR")) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute(
                "pagos",
                pagoService.listarHistorial());
        model.addAttribute(
                "membresiasClientes",
                membresiaService.listarActuales());
        model.addAttribute(
                "metodosPago",
                new MetodoPago[]{
                    MetodoPago.EFECTIVO,
                    MetodoPago.TARJETA,
                    MetodoPago.TRANSFERENCIA,
                    MetodoPago.SINPE_MOVIL
                });
        model.addAttribute(
                "fechaMinima",
                LocalDate.now().plusDays(1));
        model.addAttribute(
                "fechaProximoPagoInicial",
                LocalDate.now().plusMonths(1));
        model.addAttribute(
                "seccionActiva",
                "pagos");

        return "admin/pagos";
    }

    @PostMapping("/admin/pagos/guardar")
    public String guardarPago(
            @RequestParam Integer idMembresia,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaProximoPago,
            @RequestParam BigDecimal monto,
            @RequestParam MetodoPago metodoPago,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("ADMINISTRADOR")) {
            return "redirect:/dashboard";
        }

        try {

            pagoService.registrar(
                    idMembresia,
                    fechaProximoPago,
                    monto,
                    metodoPago);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El pago fue registrado correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/admin/pagos";
    }

    @PostMapping("/admin/pagos/eliminar/{idPago}")
    public String eliminarPago(
            @PathVariable Integer idPago,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("ADMINISTRADOR")) {
            return "redirect:/dashboard";
        }

        try {

            pagoService.eliminar(idPago);

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "El pago fue eliminado del historial.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/admin/pagos";
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
