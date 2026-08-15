package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.EjercicioRutina;
import com.ProyectoFinal.domain.EstadoMembresia;
import com.ProyectoFinal.domain.Membresia;
import com.ProyectoFinal.domain.Rutina;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.repository.MembresiaRepository;
import com.ProyectoFinal.repository.RutinaRepository;
import com.ProyectoFinal.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Capa de servicio: reglas de negocio de Rutina y sus EjercicioRutina.
// Reutilizada tanto por el entrenador (gestiona la rutina de cualquier
// cliente) como por el cliente (gestiona solo la suya): así lo pide el
// CONTEXTO_FITSYSTEM del proyecto, para no duplicar Service/Repository por rol.
@Service
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;

    public RutinaService(
            RutinaRepository rutinaRepository,
            MembresiaRepository membresiaRepository,
            UsuarioRepository usuarioRepository) {

        this.rutinaRepository = rutinaRepository;
        this.membresiaRepository = membresiaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Rutina> buscarActivaPorCliente(
            Integer idUsuario) {

        return rutinaRepository
                .findTopByClienteIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(
                        idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Rutina> listarActivas() {

        return rutinaRepository
                .findByActivaTrueOrderByClienteNombreAscFechaAsignacionDesc();
    }

    // Clientes con membresía vigente (activa y dentro de las fechas), a
    // quienes sí se les puede crear o mantener una rutina
    @Transactional(readOnly = true)
    public List<Usuario> listarClientesConMembresiaActiva() {

        return membresiaRepository
                .findMembresiasActuales()
                .stream()
                .filter(this::tieneMembresiaActiva)
                .map(Membresia::getUsuario)
                .filter(cliente -> cliente != null
                && cliente.isActivo()
                && cliente.tieneRol("CLIENTE"))
                .sorted(Comparator
                        .comparing(Usuario::getNombre)
                        .thenComparing(Usuario::getApellidos))
                .toList();
    }

    // De los clientes con membresía activa, deja solo los que todavía NO
    // tienen una rutina activa (para el selector del formulario "Agregar rutina")
    @Transactional(readOnly = true)
    public List<Usuario> listarClientesDisponibles() {

        return listarClientesConMembresiaActiva()
                .stream()
                .filter(cliente -> !rutinaRepository
                .existsByClienteIdUsuarioAndActivaTrue(
                        cliente.getIdUsuario()))
                .toList();
    }

    // Crea la rutina de un cliente, validando: cliente activo con rol CLIENTE,
    // membresía activa y vigente, y que no tenga ya otra rutina activa (solo
    // una rutina activa por cliente a la vez)
    @Transactional
    public Rutina crearRutina(
            Integer idCliente,
            String nombre,
            String objetivo,
            String descripcion,
            LocalDate fechaAsignacion) {

        validarDatosRutina(
                idCliente,
                nombre,
                objetivo,
                descripcion,
                fechaAsignacion);

        Usuario cliente = usuarioRepository
                .findById(idCliente)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El cliente seleccionado no existe."));

        if (!cliente.isActivo()
                || !cliente.tieneRol("CLIENTE")) {

            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente activo.");
        }

        Membresia membresia = membresiaRepository
                .findTopByUsuarioIdUsuarioOrderByIdMembresiaDesc(
                        idCliente)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El cliente no tiene una membresía registrada."));

        if (!tieneMembresiaActiva(membresia)) {

            throw new IllegalArgumentException(
                    "El cliente debe tener una membresía activa y vigente.");
        }

        if (rutinaRepository
                .existsByClienteIdUsuarioAndActivaTrue(
                        idCliente)) {

            throw new IllegalArgumentException(
                    "El cliente ya tiene una rutina activa.");
        }

        Rutina rutina = new Rutina();

        rutina.setCliente(cliente);
        rutina.setNombre(nombre.trim());
        rutina.setObjetivo(
                limpiarTextoOpcional(objetivo));
        rutina.setDescripcion(
                limpiarTextoOpcional(descripcion));
        rutina.setFechaAsignacion(fechaAsignacion);
        rutina.setActiva(true);

        return rutinaRepository.save(rutina);
    }

    // Al borrar la rutina, sus ejercicios se eliminan solos por la relación
    // en cascada (orphanRemoval) definida en Rutina.ejercicios
    @Transactional
    public void eliminarRutina(
            Integer idRutina) {

        if (idRutina == null) {
            throw new IllegalArgumentException(
                    "La rutina es obligatoria.");
        }

        Rutina rutina = rutinaRepository
                .findById(idRutina)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "La rutina seleccionada no existe."));

        if (!rutina.isActiva()) {
            throw new IllegalArgumentException(
                    "La rutina ya no se encuentra activa.");
        }

        rutinaRepository.delete(rutina);
    }

    // idUsuario es el cliente dueño de la rutina (si lo llama el propio
    // cliente, es su id; si lo llama el entrenador, es el id que eligió en el
    // formulario). El nuevo ejercicio se agrega al final (mayor orden + 1).
    @Transactional
    public EjercicioRutina agregarEjercicio(
            Integer idUsuario,
            String dia,
            String nombre,
            Integer series,
            String repeticiones,
            String observaciones) {

        validarDatosEjercicio(
                dia,
                nombre,
                series,
                repeticiones);

        Rutina rutina = obtenerRutinaActiva(
                idUsuario);

        int siguienteOrden = rutina
                .getEjercicios()
                .stream()
                .map(EjercicioRutina::getOrden)
                .filter(orden -> orden != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;

        EjercicioRutina ejercicio
                = new EjercicioRutina();

        ejercicio.setRutina(rutina);
        ejercicio.setOrden(siguienteOrden);

        copiarDatosEjercicio(
                ejercicio,
                dia,
                nombre,
                series,
                repeticiones,
                observaciones);

        rutina.getEjercicios().add(ejercicio);
        rutinaRepository.save(rutina);

        return ejercicio;
    }

    @Transactional
    public EjercicioRutina editarEjercicio(
            Integer idUsuario,
            Integer idEjercicio,
            String dia,
            String nombre,
            Integer series,
            String repeticiones,
            String observaciones) {

        validarDatosEjercicio(
                dia,
                nombre,
                series,
                repeticiones);

        if (idEjercicio == null) {
            throw new IllegalArgumentException(
                    "El ejercicio es obligatorio.");
        }

        Rutina rutina = obtenerRutinaActiva(
                idUsuario);

        EjercicioRutina ejercicio = rutina
                .getEjercicios()
                .stream()
                .filter(item -> idEjercicio.equals(
                        item.getIdEjercicio()))
                .findFirst()
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El ejercicio no pertenece a tu rutina activa."));

        copiarDatosEjercicio(
                ejercicio,
                dia,
                nombre,
                series,
                repeticiones,
                observaciones);

        rutinaRepository.save(rutina);

        return ejercicio;
    }

    @Transactional
    public void eliminarEjercicio(
            Integer idUsuario,
            Integer idEjercicio) {

        if (idEjercicio == null) {
            throw new IllegalArgumentException(
                    "El ejercicio es obligatorio.");
        }

        Rutina rutina = obtenerRutinaActiva(
                idUsuario);

        EjercicioRutina ejercicio = rutina
                .getEjercicios()
                .stream()
                .filter(item -> idEjercicio.equals(
                        item.getIdEjercicio()))
                .findFirst()
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "El ejercicio no pertenece a tu rutina activa."));

        rutina.getEjercicios().remove(ejercicio);
        rutinaRepository.save(rutina);
    }

    // Punto único para ubicar "la rutina sobre la que se está trabajando";
    // lanza error si el cliente no tiene ninguna rutina activa
    private Rutina obtenerRutinaActiva(
            Integer idUsuario) {

        if (idUsuario == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio.");
        }

        return rutinaRepository
                .findTopByClienteIdUsuarioAndActivaTrueOrderByFechaAsignacionDesc(
                        idUsuario)
                .orElseThrow(() ->
                new IllegalArgumentException(
                        "No tienes una rutina activa."));
    }

    private void copiarDatosEjercicio(
            EjercicioRutina ejercicio,
            String dia,
            String nombre,
            Integer series,
            String repeticiones,
            String observaciones) {

        ejercicio.setDia(dia.trim());
        ejercicio.setNombre(nombre.trim());
        ejercicio.setSeries(series);
        ejercicio.setRepeticiones(
                repeticiones.trim());
        ejercicio.setObservaciones(
                limpiarTextoOpcional(observaciones));
    }

    private void validarDatosEjercicio(
            String dia,
            String nombre,
            Integer series,
            String repeticiones) {

        if (!tieneTexto(dia)) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un día.");
        }

        if (!tieneTexto(nombre)) {
            throw new IllegalArgumentException(
                    "El nombre del ejercicio es obligatorio.");
        }

        if (series == null || series <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de series debe ser mayor que cero.");
        }

        if (!tieneTexto(repeticiones)) {
            throw new IllegalArgumentException(
                    "Las repeticiones son obligatorias.");
        }
    }

    private void validarDatosRutina(
            Integer idCliente,
            String nombre,
            String objetivo,
            String descripcion,
            LocalDate fechaAsignacion) {

        if (idCliente == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un cliente.");
        }

        if (!tieneTexto(nombre)) {
            throw new IllegalArgumentException(
                    "El nombre de la rutina es obligatorio.");
        }

        if (nombre.trim().length() > 80) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar 80 caracteres.");
        }

        if (tieneTexto(objetivo)
                && objetivo.trim().length() > 255) {
            throw new IllegalArgumentException(
                    "El objetivo no puede superar 255 caracteres.");
        }

        if (tieneTexto(descripcion)
                && descripcion.trim().length() > 500) {
            throw new IllegalArgumentException(
                    "La descripción no puede superar 500 caracteres.");
        }

        if (fechaAsignacion == null) {
            throw new IllegalArgumentException(
                    "La fecha de asignación es obligatoria.");
        }
    }

    // Una membresía cuenta como activa si su estado es ACTIVA y hoy cae
    // entre fecha de inicio y fecha de vencimiento (fechas null = sin límite)
    private boolean tieneMembresiaActiva(
            Membresia membresia) {

        if (membresia == null
                || membresia.getEstado()
                != EstadoMembresia.ACTIVA) {
            return false;
        }

        LocalDate hoy = LocalDate.now();

        boolean yaInicio
                = membresia.getFechaInicio() == null
                || !membresia.getFechaInicio().isAfter(hoy);

        boolean noHaVencido
                = membresia.getFechaVencimiento() == null
                || !membresia.getFechaVencimiento().isBefore(hoy);

        return yaInicio && noHaVencido;
    }

    private String limpiarTextoOpcional(
            String texto) {

        return tieneTexto(texto)
                ? texto.trim()
                : null;
    }

    private boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
