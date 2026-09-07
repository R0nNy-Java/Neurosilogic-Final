package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.service.NegocioException;
import com.rrparedes.neurosilogic.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/login"})
    public String showLogin(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String usuario,
                               @RequestParam String contrasena,
                               HttpSession session,
                               Model model) {
        try {
            Usuario u = usuarioService.autenticar(usuario, contrasena);
            session.setAttribute("usuarioLogueado", u);
            return "redirect:/dashboard";
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Usuario uSesion = (Usuario) session.getAttribute("usuarioLogueado");
        if (uSesion != null) {
            usuarioService.registrarLogout(uSesion);
        }
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/nueva-cuenta")
    public String showNuevaCuenta() {
        return "nueva_cuenta";
    }

    @PostMapping("/nueva-cuenta")
    public String processNuevaCuenta(@RequestParam String usuario,
                                     @RequestParam String contrasena,
                                     @RequestParam String nombreCompleto,
                                     @RequestParam String email,
                                     Model model) {
        try {
            usuarioService.registrarCuenta(usuario, contrasena, nombreCompleto, email);
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            return "nueva_cuenta";
        }
        model.addAttribute("mensaje", "Solicitud de cuenta enviada exitosamente. El administrador debe asignar su rol y activar su cuenta antes de iniciar sesión.");
        return "login";
    }

    @GetMapping("/olvido-contrasena")
    public String showOlvidoContrasena() {
        return "olvido_contrasena";
    }

    @PostMapping("/olvido-contrasena")
    public String processOlvidoContrasena(@RequestParam String usuario, Model model) {
        try {
            usuarioService.recuperarContrasena(usuario);
            model.addAttribute("mensaje", "Se ha enviado una contraseña temporal a su correo registrado.");
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "olvido_contrasena";
    }

    @GetMapping("/recuperar-usuario")
    public String showRecuperarUsuario() {
        return "recuperar_usuario";
    }

    @PostMapping("/recuperar-usuario")
    public String processRecuperarUsuario(@RequestParam String email, Model model) {
        try {
            usuarioService.recuperarNombreUsuario(email);
            model.addAttribute("mensaje", "Se ha enviado tu nombre de usuario al correo ingresado.");
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "recuperar_usuario";
    }

    @GetMapping("/cambio-contrasena")
    public String showCambioContrasena(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) {
            return "redirect:/login";
        }
        return "cambio_contrasena";
    }

    @PostMapping("/cambio-contrasena")
    public String processCambioContrasena(@RequestParam String contrasenaActual,
                                          @RequestParam String nuevaContrasena,
                                          HttpSession session,
                                          Model model) {
        Usuario uSesion = (Usuario) session.getAttribute("usuarioLogueado");
        if (uSesion == null) return "redirect:/login";

        try {
            Usuario actualizado = usuarioService.cambiarContrasena(uSesion.getIdUsuario(), contrasenaActual, nuevaContrasena);
            session.setAttribute("usuarioLogueado", actualizado);
            model.addAttribute("mensaje", "Contraseña actualizada con éxito.");
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
        }
        return "cambio_contrasena";
    }
}
