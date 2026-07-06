package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.RolUsuario;
import com.ProyectoFinal.domain.Usuario;
import com.ProyectoFinal.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/acceso")
    public String acceso() {
        return "auth/acceso";
    }

    @GetMapping("/registro")
    public String registro() {
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@RequestParam String nombre,
                            @RequestParam String apellidos,
                            @RequestParam String correo,
                            @RequestParam String telefono,
                            @RequestParam String username,
                            @RequestParam String password,
                            RedirectAttributes redirectAttributes) {

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setUsername(username);
        usuario.setPassword(password);

        boolean registrado = usuarioService.registrar(usuario);

        if (!registrado) {
            redirectAttributes.addFlashAttribute("error", "El usuario ya existe.");
            return "redirect:/registro";
        }

        redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada correctamente.");
        return "redirect:/login/cliente";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/login/cliente")
    public String loginCliente() {
        return "auth/loginCliente";
    }

    @GetMapping("/login/entrenador")
    public String loginEntrenador() {
        return "auth/loginEntrenador";
    }

    @GetMapping("/login/admin")
    public String loginAdmin() {
        return "auth/loginAdmin";
    }

    @PostMapping("/login/cliente")
    public String entrarCliente(@RequestParam String username,
                                @RequestParam String password,
                                RedirectAttributes redirectAttributes) {

        var usuario = usuarioService.login(username, password, RolUsuario.CLIENTE);

        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/login/cliente";
        }

        return "redirect:/cliente";
    }

    @PostMapping("/login/entrenador")
    public String entrarEntrenador(@RequestParam String username,
                                   @RequestParam String password,
                                   RedirectAttributes redirectAttributes) {

        var usuario = usuarioService.login(username, password, RolUsuario.ENTRENADOR);

        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/login/entrenador";
        }

        return "redirect:/entrenador";
    }

    @PostMapping("/login/admin")
    public String entrarAdmin(@RequestParam String username,
                              @RequestParam String password,
                              RedirectAttributes redirectAttributes) {

        var usuario = usuarioService.login(username, password, RolUsuario.ADMINISTRADOR);

        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario o contraseña incorrectos");
            return "redirect:/login/admin";
        }

        return "redirect:/admin";
    }

}