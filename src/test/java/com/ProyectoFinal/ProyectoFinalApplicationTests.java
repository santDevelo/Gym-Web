package com.ProyectoFinal;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProyectoFinalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void dashboardClienteSeRenderiza() throws Exception {

        var sesion = crearSesion(
                "sebastian",
                "CLIENTE");

        mockMvc.perform(
                get("/dashboard").session(sesion))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Mi resumen")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString(
                                "Rutina de hoy"))))
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString(
                                "Asistencias recientes"))))
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString(
                                "Accesos rápidos"))));
    }

    @Test
    void vistasEntrenadorSeRenderizanSinCitas()
            throws Exception {

        var sesion = crearSesion(
                "cruiz",
                "ENTRENADOR");

        mockMvc.perform(
                get("/dashboard").session(sesion))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Panel del entrenador")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Rutinas activas")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Ejercicios asignados")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Disponibles para rutina")));

        mockMvc.perform(
                get("/entrenador/clientes").session(sesion))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Clientes activos")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.not(
                                org.hamcrest.Matchers.containsString(
                                        ">Citas<"))));

        mockMvc.perform(
                get("/entrenador/rutinas").session(sesion))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Rutinas activas")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Agregar rutina")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.matchesPattern(
                                "(?s).*<button(?=[^>]*data-bs-target="
                                + "\"#modalAgregarRutina\")"
                                + "(?![^>]*disabled)[^>]*>.*")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "/entrenador/rutinas/agregar")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "/entrenador/rutinas/eliminar")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "name=\"idCliente\"")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Gestionar ejercicios")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "/entrenador/rutinas/ejercicios/agregar")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "modalAgregarEjercicioEntrenador")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "/entrenador/rutinas/ejercicios/editar")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Guardar cambios")))
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "/entrenador/rutinas/ejercicios/eliminar")));

        mockMvc.perform(
                get("/entrenador/progreso").session(sesion))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        org.hamcrest.Matchers.containsString(
                                "Seguimiento de actividad")));
    }

    private MockHttpSession crearSesion(
            String username,
            String rol) {

        var autenticacion
                = new UsernamePasswordAuthenticationToken(
                        username,
                        "",
                        List.of(new SimpleGrantedAuthority(
                                "ROLE_" + rol)));

        var contextoSeguridad
                = new SecurityContextImpl(autenticacion);

        var sesion = new MockHttpSession();

        sesion.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                contextoSeguridad);

        return sesion;
    }
}
