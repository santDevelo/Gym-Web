package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Asistencia;
import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.MetodoPago;
import com.ProyectoFinal.domain.NombreRol;
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

    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard";
    private static final String REDIRECT_MEMBRESIAS = "redirect:/admin/membresias";
    private static final String REDIRECT_PAGOS = "redirect:/admin/pagos";

    private static final MetodoPago[] METODOS_PAGO_REGISTRABLES = {
        MetodoPago.EFECTIVO,
        MetodoPago.TARJETA,
        MetodoPago.TRANSFERENCIA,
        MetodoPago.SINPE_MOVIL
    };

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
            RutinaService rutinaService) {
        this.usuarioService = usuarioService;
        this.membresiaService = membresiaService;
        this.planMembresiaService = planMembresiaService;
        this.pagoService = pagoService;
        this.asistenciaService = asistenciaService;
        this.rutinaService = rutinaService;
    }

    @GetMapping({"/dashboard", "/dashboard/"})
    public String mostrarDashboard(Principal principal, Model model) {
        Usuario usuario = obtenerUsuarioAutenticado(principal);
        model.addAttribute("usuario", usuario);

        if (usuario.tieneRol(NombreRol.ADMINISTRADOR)) {
            cargarResumenAdministrador(model);
        }
        if (usuario.tieneRol(NombreRol.ENTRENADOR)) {
            cargarResumenEntrenador(model);
        }
        if (usuario.tieneRol(NombreRol.CLIENTE)) {
            cargarResumenCliente(model, usuario);
        }

        model.addAttribute("seccionActiva", "dashboard");
        return "dashboard/listado";
    }

    @GetMapping("/cliente/membresia")
    public String mostrarMembresiaCliente(Principal principal, Model model) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        model.addAttribute("usuario", cliente);
        model.addAttribute(
                "membresia",
                membresiaService.buscarUltimaPorUsuario(cliente.getIdUsuario()).orElse(null));
        model.addAttribute("seccionActiva", "cliente-membresia");
        return "cliente/membresia";
    }

    @PostMapping("/cliente/membresia/cancelar")
    public String cancelarMembresiaCliente(
            Principal principal,
            RedirectAttributes atributos) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> membresiaService.inactivarMembresiaCliente(cliente.getIdUsuario()),
                atributos,
                "Tu membresía fue inactivada correctamente.");
        return "redirect:/cliente/membresia";
    }

    @GetMapping("/cliente/perfil")
    public String mostrarPerfilCliente(Principal principal, Model model) {
        Usuario cliente = obtenerUsuarioAutenticado(principal);
        if (!cliente.tieneRol(NombreRol.CLIENTE)) {
            return REDIRECT_DASHBOARD;
        }

        model.addAttribute("usuario", cliente);
        model.addAttribute("seccionActiva", "cliente-perfil");
        return "cliente/perfil";
    }

    @GetMapping("/entrenador/clientes")
    public String mostrarClientesEntrenador(Principal principal, Model model) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        List<Usuario> clientes = usuarioService.listarActivosPorRol(NombreRol.CLIENTE);
        model.addAttribute("usuario", entrenador);
        model.addAttribute("clientes", clientes);
        model.addAttribute("membresiasPorCliente", obtenerMembresiasPorCliente(clientes));
        model.addAttribute("seccionActiva", "entrenador-clientes");
        return "entrenador/clientes";
    }

    @GetMapping("/entrenador/progreso")
    public String mostrarProgresoEntrenador(Principal principal, Model model) {
        Usuario entrenador = obtenerUsuarioAutenticado(principal);
        if (!entrenador.tieneRol(NombreRol.ENTRENADOR)) {
            return REDIRECT_DASHBOARD;
        }

        List<Usuario> clientes = usuarioService.listarActivosPorRol(NombreRol.CLIENTE);
        cargarProgresoClientes(model, clientes);
        model.addAttribute("usuario", entrenador);
        model.addAttribute("clientes", clientes);
        model.addAttribute("seccionActiva", "entrenador-progreso");
        return "entrenador/progreso";
    }

    @GetMapping("/admin/membresias")
    public String mostrarMembresias(Principal principal, Model model) {
        Usuario administrador = obtenerUsuarioAutenticado(principal);
        if (!administrador.tieneRol(NombreRol.ADMINISTRADOR)) {
            return REDIRECT_DASHBOARD;
        }

        model.addAttribute("usuario", administrador);
        model.addAttribute("planes", planMembresiaService.listarTodos());
        model.addAttribute("seccionActiva", "membresias");
        return "admin/membresias";
    }

    @PostMapping("/admin/membresias/guardar")
    public String guardarPlanMembresia(
            @RequestParam Integer idPlan,
            @RequestParam String nombre,
            @RequestParam(name = "descripcion", required = false) String descripcion,
            @RequestParam BigDecimal precio,
            @RequestParam(name = "activo", defaultValue = "false") boolean activo,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario administrador = obtenerUsuarioAutenticado(principal);
        if (!administrador.tieneRol(NombreRol.ADMINISTRADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> planMembresiaService.actualizar(
                        idPlan, nombre, descripcion, precio, activo),
                atributos,
                "El plan fue actualizado correctamente.");
        return REDIRECT_MEMBRESIAS;
    }

    @GetMapping("/admin/pagos")
    public String mostrarPagos(Principal principal, Model model) {
        Usuario administrador = obtenerUsuarioAutenticado(principal);
        if (!administrador.tieneRol(NombreRol.ADMINISTRADOR)) {
            return REDIRECT_DASHBOARD;
        }

        LocalDate hoy = LocalDate.now();
        model.addAttribute("usuario", administrador);
        model.addAttribute("pagos", pagoService.listarHistorial());
        model.addAttribute("membresiasClientes", membresiaService.listarActuales());
        model.addAttribute("metodosPago", METODOS_PAGO_REGISTRABLES);
        model.addAttribute("fechaMinima", hoy.plusDays(1));
        model.addAttribute("fechaProximoPagoInicial", hoy.plusMonths(1));
        model.addAttribute("seccionActiva", "pagos");
        return "admin/pagos";
    }

    @PostMapping("/admin/pagos/guardar")
    public String guardarPago(
            @RequestParam Integer idMembresia,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaProximoPago,
            @RequestParam BigDecimal monto,
            @RequestParam MetodoPago metodoPago,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario administrador = obtenerUsuarioAutenticado(principal);
        if (!administrador.tieneRol(NombreRol.ADMINISTRADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> pagoService.registrar(
                        idMembresia, fechaProximoPago, monto, metodoPago),
                atributos,
                "El pago fue registrado correctamente.");
        return REDIRECT_PAGOS;
    }

    @PostMapping("/admin/pagos/eliminar/{idPago}")
    public String eliminarPago(
            @PathVariable Integer idPago,
            Principal principal,
            RedirectAttributes atributos) {
        Usuario administrador = obtenerUsuarioAutenticado(principal);
        if (!administrador.tieneRol(NombreRol.ADMINISTRADOR)) {
            return REDIRECT_DASHBOARD;
        }

        ejecutarAccion(
                () -> pagoService.eliminar(idPago),
                atributos,
                "El pago fue eliminado del historial.");
        return REDIRECT_PAGOS;
    }

    private void cargarResumenAdministrador(Model model) {
        model.addAttribute(
                "clientesActivos", usuarioService.contarPorRolActivo(NombreRol.CLIENTE));
        model.addAttribute(
                "entrenadoresActivos", usuarioService.contarPorRolActivo(NombreRol.ENTRENADOR));
        model.addAttribute("ingresosMes", membresiaService.ingresosDelMes());
        model.addAttribute(
                "pagosPendientes", membresiaService.contarPorEstado(EstadoMembresia.PENDIENTE));
    }

    private void cargarResumenEntrenador(Model model) {
        var rutinasActivas = rutinaService.listarActivas();
        int totalEjercicios = rutinasActivas.stream()
                .mapToInt(rutina -> rutina.getEjercicios().size())
                .sum();

        model.addAttribute(
                "clientesActivosEntrenador",
                usuarioService.contarPorRolActivo(NombreRol.CLIENTE));
        model.addAttribute("totalRutinasActivas", rutinasActivas.size());
        model.addAttribute("totalEjerciciosAsignados", totalEjercicios);
        model.addAttribute(
                "clientesDisponiblesRutina", rutinaService.listarClientesDisponibles().size());
    }

    private void cargarResumenCliente(Model model, Usuario cliente) {
        Integer idCliente = cliente.getIdUsuario();
        model.addAttribute(
                "membresia", membresiaService.buscarUltimaPorUsuario(idCliente).orElse(null));
        model.addAttribute(
                "totalAsistenciasMes", asistenciaService.contarAsistenciasDelMes(idCliente));
    }

    private void cargarProgresoClientes(Model model, List<Usuario> clientes) {
        Map<Integer, Long> asistenciasMes = new LinkedHashMap<>();
        Map<Integer, Asistencia> ultimasAsistencias = new LinkedHashMap<>();
        Map<Integer, Integer> ejerciciosActivos = new LinkedHashMap<>();
        long totalAsistencias = 0;
        long clientesConRutina = 0;

        for (Usuario cliente : clientes) {
            Integer idCliente = cliente.getIdUsuario();
            long visitas = asistenciaService.contarAsistenciasDelMes(idCliente);
            int cantidadEjercicios = rutinaService.buscarActivaPorCliente(idCliente)
                    .map(rutina -> rutina.getEjercicios().size())
                    .orElse(0);

            asistenciasMes.put(idCliente, visitas);
            ejerciciosActivos.put(idCliente, cantidadEjercicios);
            asistenciaService.buscarUltimaPorCliente(idCliente)
                    .ifPresent(asistencia -> ultimasAsistencias.put(idCliente, asistencia));

            totalAsistencias += visitas;
            if (cantidadEjercicios > 0) {
                clientesConRutina++;
            }
        }

        model.addAttribute("asistenciasMes", asistenciasMes);
        model.addAttribute("ultimasAsistencias", ultimasAsistencias);
        model.addAttribute("ejerciciosActivos", ejerciciosActivos);
        model.addAttribute("totalAsistenciasMes", totalAsistencias);
        model.addAttribute("clientesConRutina", clientesConRutina);
    }

    private Usuario obtenerUsuarioAutenticado(Principal principal) {
        return usuarioService.buscarPorUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "El usuario autenticado no existe."));
    }

    private Map<Integer, Membresia> obtenerMembresiasPorCliente(List<Usuario> clientes) {
        Map<Integer, Membresia> membresias = new LinkedHashMap<>();
        for (Usuario cliente : clientes) {
            membresiaService.buscarUltimaPorUsuario(cliente.getIdUsuario())
                    .ifPresent(membresia -> membresias.put(cliente.getIdUsuario(), membresia));
        }
        return membresias;
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
