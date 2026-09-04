package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.service.AuditoriaAccesoService;
import com.rrparedes.neurosilogic.service.EmailService;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.SecureRandom;
import java.util.Optional;

@Controller
public class AuthController {

    // Cantidad de intentos fallidos consecutivos antes de bloquear la cuenta automáticamente
    private static final int MAX_INTENTOS_FALLIDOS = 5;

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaAccesoService auditoriaAccesoService;
    private final EmailService emailService;

    public AuthController(UsuarioRepository usuarioRepository, AuditoriaAccesoService auditoriaAccesoService,
                          EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaAccesoService = auditoriaAccesoService;
        this.emailService = emailService;
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
            if (u.isBloqueado()) {
                model.addAttribute("error", "La cuenta se encuentra bloqueada. Contacte al administrador.");
                return "login";
            }
            if (PasswordUtil.verificar(contrasena, u.getContrasenaHash())) {
                u.setIntentosFallidos(0);
                usuarioRepository.save(u);
                session.setAttribute("usuarioLogueado", u);
                auditoriaAccesoService.registrar(u, "LOGIN_EXITOSO");
                return "redirect:/dashboard";
            }

            // Contraseña incorrecta: contamos el intento y bloqueamos automáticamente al llegar al máximo
            u.setIntentosFallidos(u.getIntentosFallidos() + 1);
            if (u.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
                u.setBloqueado(true);
                usuarioRepository.save(u);
                auditoriaAccesoService.registrar(u, "BLOQUEO_AUTOMATICO",
                        "Cuenta bloqueada tras " + MAX_INTENTOS_FALLIDOS + " intentos fallidos consecutivos.");
                model.addAttribute("error", "La cuenta ha sido bloqueada por demasiados intentos fallidos. Contacte al administrador.");
                return "login";
            }
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(u, "LOGIN_FALLIDO",
                    "Intento " + u.getIntentosFallidos() + " de " + MAX_INTENTOS_FALLIDOS + ".");
        }
        model.addAttribute("error", "Usuario o contraseña incorrectos.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        Usuario uSesion = (Usuario) session.getAttribute("usuarioLogueado");
        if (uSesion != null) {
            auditoriaAccesoService.registrar(uSesion, "LOGOUT");
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
        if (uOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el usuario ingresado.");
            return "olvido_contrasena";
        }

        Usuario u = uOpt.get();
        if (u.getEmail() == null || u.getEmail().isBlank()) {
            model.addAttribute("error", "Este usuario no tiene un correo registrado. Contacte al administrador.");
            return "olvido_contrasena";
        }

        String contrasenaTemporal = generarContrasenaTemporal();
        try {
            emailService.enviarCorreo(u.getEmail(), "NurseLogic Digital - Recuperación de contraseña",
                    "Se generó una contraseña temporal para su cuenta \"" + u.getNombreUsuario() + "\":\n\n"
                            + contrasenaTemporal
                            + "\n\nInicie sesión con ella y cámbiela de inmediato desde su perfil.\n"
                            + "Si usted no solicitó este cambio, contacte al administrador del sistema.");
        } catch (MailException ex) {
            // No se aplica el cambio si el correo no pudo enviarse: evita dejar al usuario
            // con una contraseña que nunca recibió.
            model.addAttribute("error", "No se pudo enviar el correo de recuperación. Intente más tarde o contacte al administrador.");
            return "olvido_contrasena";
        }

        u.setContrasenaHash(PasswordUtil.hash(contrasenaTemporal));
        u.setIntentosFallidos(0);
        u.setBloqueado(false);
        usuarioRepository.save(u);
        auditoriaAccesoService.registrar(u, "RESET_CONTRASENA", "Contraseña temporal enviada por correo.");
        model.addAttribute("mensaje", "Se ha enviado una contraseña temporal a su correo registrado.");
        return "olvido_contrasena";
    }

    private String generarContrasenaTemporal() {
        String alfabeto = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(alfabeto.charAt(random.nextInt(alfabeto.length())));
        }
        return sb.toString();
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
