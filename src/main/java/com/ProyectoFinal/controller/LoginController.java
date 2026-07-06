package com.ProyectoFinal.controller;

import com.ProyectoFinal.domain.Login;
import com.ProyectoFinal.domain.Rol;
import com.ProyectoFinal.service.LoginService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final LoginService usuarioService;

    public LoginController(LoginService usuarioService) {
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
    public String registrar(
            @RequestParam String nombre,
            @RequestParam String apellidos,
            @RequestParam String correo,
            @RequestParam String telefono,
            @RequestParam String username,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {

        Login usuario = new Login();

        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setUsername(username);
        usuario.setPassword(password);

        boolean registrado = usuarioService.registrar(usuario);

        if (!registrado) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "El usuario ya existe."
            );

            return "redirect:/registro";
        }

        redirectAttributes.addFlashAttribute(
                "mensaje",
                "Cuenta creada correctamente."
        );

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
    public String entrarCliente(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        var usuario = usuarioService.login(
                username,
                password,
                Rol.CLIENTE
        );

        if (usuario.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Usuario o contraseña incorrectos."
            );

            return "redirect:/login/cliente";
        }

    
        session.setAttribute(
                "idUsuario",
                usuario.get().getIdUsuario()
        );

        return "redirect:/cliente";
    }


    @PostMapping("/login/entrenador")
    public String entrarEntrenador(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        var usuario = usuarioService.login(
                username,
                password,
                Rol.ENTRENADOR
        );

        if (usuario.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Usuario o contraseña incorrectos."
            );

            return "redirect:/login/entrenador";
        }

       
        session.setAttribute(
                "idUsuario",
                usuario.get().getIdUsuario()
        );

        return "redirect:/entrenador";
    }


    @PostMapping("/login/admin")
    public String entrarAdmin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {

        var usuario = usuarioService.login(
                username,
                password,
                Rol.ADMINISTRADOR
        );

        if (usuario.isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Usuario o contraseña incorrectos."
            );

            return "redirect:/login/admin";
        }

   
        session.setAttribute(
                "idUsuario",
                usuario.get().getIdUsuario()
        );

        return "redirect:/admin";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }
}