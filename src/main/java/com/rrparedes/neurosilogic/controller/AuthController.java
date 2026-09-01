package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
        Optional<Usuario> uOpt = usuarioRepository.findByNombreUsuarioIgnoreCase(usuario.trim());
        if (uOpt.isPresent()) {
            Usuario u = uOpt.get();
            if (u.isBloqueado() || "B".equalsIgnoreCase(u.getEstado())) {
                model.addAttribute("error", "La cuenta se encuentra bloqueada. Contacte al administrador.");
                return "login";
            }
            if (PasswordUtil.verificar(contrasena, u.getContrasenaHash())) {
                session.setAttribute("usuarioLogueado", u);
                return "redirect:/dashboard";
            }
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
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
        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(usuario.trim())) {
            model.addAttribute("error", "El nombre de usuario ya existe.");
            return "nueva_cuenta";
        }
        Usuario u = new Usuario(usuario.trim(), PasswordUtil.hash(contrasena), "ENFERMERO", nombreCompleto, email, false);
        usuarioRepository.save(u);
        model.addAttribute("mensaje", "Cuenta creada exitosamente. Ya puede iniciar sesión.");
        return "login";
    }

    @GetMapping("/olvido-contrasena")
    public String showOlvidoContrasena() {
        return "olvido_contrasena";
    }

    @PostMapping("/olvido-contrasena")
    public String processOlvidoContrasena(@RequestParam String usuario, Model model) {
        Optional<Usuario> uOpt = usuarioRepository.findByNombreUsuarioIgnoreCase(usuario.trim());
        if (uOpt.isPresent()) {
            model.addAttribute("mensaje", "Se han enviado las instrucciones de recuperación al correo registrado.");
        } else {
            model.addAttribute("error", "No se encontró el usuario ingresado.");
        }
        return "olvido_contrasena";
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

        Optional<Usuario> uOpt = usuarioRepository.findById(uSesion.getIdUsuario());
        if (uOpt.isPresent()) {
            Usuario u = uOpt.get();
            if (PasswordUtil.verificar(contrasenaActual, u.getContrasenaHash())) {
                u.setContrasenaHash(PasswordUtil.hash(nuevaContrasena));
                usuarioRepository.save(u);
                session.setAttribute("usuarioLogueado", u);
                model.addAttribute("mensaje", "Contraseña actualizada con éxito.");
                return "cambio_contrasena";
            }
        }
        model.addAttribute("error", "La contraseña actual no es correcta.");
        return "cambio_contrasena";
    }
}
