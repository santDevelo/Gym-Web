package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Asistencia;
import com.ProyectoFinal.domain.MetodoPago;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.AsistenciaService;
import com.ProyectoFinal.service.MembresiaService;
import com.ProyectoFinal.service.PagoService;
import com.ProyectoFinal.service.PlanMembresiaService;
import com.ProyectoFinal.service.RutinaService;
import com.ProyectoFinal.service.UsuarioService;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final AsistenciaService asistenciaService;
    private final RutinaService rutinaService;

    public DashboardController(
            UsuarioService usuarioService,
            MembresiaService membresiaService,
            PlanMembresiaService planMembresiaService,
            PagoService pagoService,
            AsistenciaService asistenciaService,
            RutinaService rutinaService
    ) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
        this.planMembresiaService = planMembresiaService;
        this.pagoService = pagoService;
        this.asistenciaService = asistenciaService;
        this.rutinaService = rutinaService;
    }

    @GetMapping({
        "/dashboard",
        "/dashboard/"
    })
    public String mostrarDashboard(Principal principal,Model model){
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

            var rutinasActivas
                    = rutinaService.listarActivas();

            model.addAttribute(
                    "clientesActivosEntrenador",
                    usuarioService.contarPorRolActivo(
                            "CLIENTE"
                    )
            );

            model.addAttribute(
                    "totalRutinasActivas",
                    rutinasActivas.size());

            model.addAttribute(
                    "totalEjerciciosAsignados",
                    rutinasActivas
                            .stream()
                            .mapToInt(rutina -> rutina
                            .getEjercicios()
                            .size())
                            .sum());

            model.addAttribute(
                    "clientesDisponiblesRutina",
                    rutinaService
                            .listarClientesDisponibles()
                            .size());
        }

        if (usuario.tieneRol("CLIENTE")) {

            Integer idUsuario = usuario.getIdUsuario();

            Membresia membresia = membresiaService
                    .buscarUltimaPorUsuario(idUsuario)
                    .orElse(null);

            model.addAttribute("membresia", membresia);
            model.addAttribute(
                    "totalAsistenciasMes",
                    asistenciaService.contarAsistenciasDelMes(
                            idUsuario));
        }

        model.addAttribute("seccionActiva", "dashboard");

        return "dashboard/listado";
    }

    /*
 * Muestra la membresía del cliente autenticado.
 */
@GetMapping("/cliente/membresia")
public String mostrarMembresiaCliente(
        Principal principal,
        Model model) {

    Usuario usuario
            = obtenerUsuarioAutenticado(principal);

    if (!usuario.tieneRol("CLIENTE")) {
        return "redirect:/dashboard";
    }

    model.addAttribute(
            "usuario",
            usuario);

    model.addAttribute(
            "membresia",
            membresiaService
                    .buscarUltimaPorUsuario(
                            usuario.getIdUsuario())
                    .orElse(null));

    model.addAttribute(
            "seccionActiva",
            "cliente-membresia");

    return "cliente/membresia";
}

    /*
     * Inactiva la membresía del cliente autenticado.
     */
    @PostMapping("/cliente/membresia/cancelar")
    public String cancelarMembresiaCliente(
            Principal principal,
            RedirectAttributes redirectAttributes) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        try {

            membresiaService.inactivarMembresiaCliente(
                    usuario.getIdUsuario());

            redirectAttributes.addFlashAttribute(
                    "todoOk",
                    "Tu membresía fue inactivada correctamente.");

        } catch (IllegalArgumentException ex) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage());
        }

        return "redirect:/cliente/membresia";
    }

    /*
     * Muestra los datos del perfil del cliente autenticado.
     */
    @GetMapping("/cliente/perfil")
    public String mostrarPerfilCliente(
            Principal principal,
            Model model) {

        Usuario usuario
                = obtenerUsuarioAutenticado(principal);

        if (!usuario.tieneRol("CLIENTE")) {
            return "redirect:/dashboard";
        }

        model.addAttribute(
                "usuario",
                usuario);

        model.addAttribute(
                "seccionActiva",
                "cliente-perfil");

        return "cliente/perfil";
    }

    @GetMapping("/entrenador/clientes")
    public String mostrarClientesEntrenador(
            Principal principal,
            Model model) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        List<Usuario> clientes
                = usuarioService.listarActivosPorRol(
                        "CLIENTE");

        model.addAttribute("usuario", entrenador);
        model.addAttribute("clientes", clientes);
        model.addAttribute(
                "membresiasPorCliente",
                obtenerMembresiasPorCliente(clientes));
        model.addAttribute(
                "seccionActiva",
                "entrenador-clientes");

        return "entrenador/clientes";
    }

    @GetMapping("/entrenador/progreso")
    public String mostrarProgresoEntrenador(
            Principal principal,
            Model model) {

        Usuario entrenador
                = obtenerUsuarioAutenticado(principal);

        if (!entrenador.tieneRol("ENTRENADOR")) {
            return "redirect:/dashboard";
        }

        List<Usuario> clientes
                = usuarioService.listarActivosPorRol(
                        "CLIENTE");

        Map<Integer, Long> asistenciasMes
                = new LinkedHashMap<>();
        Map<Integer, Asistencia> ultimasAsistencias
                = new LinkedHashMap<>();
        Map<Integer, Integer> ejerciciosActivos
                = new LinkedHashMap<>();

        long totalAsistencias = 0;
        long clientesConRutina = 0;

        for (Usuario cliente : clientes) {

            Integer idCliente = cliente.getIdUsuario();

            long visitas = asistenciaService
                    .contarAsistenciasDelMes(idCliente);

            asistenciasMes.put(idCliente, visitas);
            totalAsistencias += visitas;

            asistenciaService
                    .buscarUltimaPorCliente(idCliente)
                    .ifPresent(asistencia ->
                    ultimasAsistencias.put(
                            idCliente,
                            asistencia));

            int cantidadEjercicios = rutinaService
                    .buscarActivaPorCliente(idCliente)
                    .map(rutina -> rutina
                    .getEjercicios()
                    .size())
                    .orElse(0);

            ejerciciosActivos.put(
                    idCliente,
                    cantidadEjercicios);

            if (cantidadEjercicios > 0) {
                clientesConRutina++;
            }
        }

        model.addAttribute("usuario", entrenador);
        model.addAttribute("clientes", clientes);
        model.addAttribute("asistenciasMes", asistenciasMes);
        model.addAttribute(
                "ultimasAsistencias",
                ultimasAsistencias);
        model.addAttribute(
                "ejerciciosActivos",
                ejerciciosActivos);
        model.addAttribute(
                "totalAsistenciasMes",
                totalAsistencias);
        model.addAttribute(
                "clientesConRutina",
                clientesConRutina);
        model.addAttribute(
                "seccionActiva",
                "entrenador-progreso");

        return "entrenador/progreso";
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

    private Map<Integer, Membresia>
            obtenerMembresiasPorCliente(
                    List<Usuario> clientes) {

        Map<Integer, Membresia> membresias
                = new LinkedHashMap<>();

        for (Usuario cliente : clientes) {

            membresiaService
                    .buscarUltimaPorUsuario(
                            cliente.getIdUsuario())
                    .ifPresent(membresia ->
                    membresias.put(
                            cliente.getIdUsuario(),
                            membresia));
        }

        return membresias;
    }
}
