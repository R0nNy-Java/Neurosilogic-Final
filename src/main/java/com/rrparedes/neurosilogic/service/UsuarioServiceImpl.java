package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    // Cantidad de intentos fallidos consecutivos antes de bloquear la cuenta automáticamente
    private static final int MAX_INTENTOS_FALLIDOS = 5;

    private final UsuarioRepository usuarioRepository;
    private final AuditoriaAccesoService auditoriaAccesoService;
    private final EmailService emailService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, AuditoriaAccesoService auditoriaAccesoService,
                              EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaAccesoService = auditoriaAccesoService;
        this.emailService = emailService;
    }

    @Override
    public Usuario autenticar(String usuario, String contrasena) {
        Usuario u = usuarioRepository.findByNombreUsuarioIgnoreCase(usuario.trim())
                .orElseThrow(() -> new NegocioException("Usuario o contraseña incorrectos."));

        if (u.isBloqueado() || "SIN_ASIGNAR".equalsIgnoreCase(u.getRol()) || u.getRol() == null) {
            throw new NegocioException("La cuenta está pendiente de aprobación o asignación de rol por el administrador.");
        }

        if (PasswordUtil.verificar(contrasena, u.getContrasenaHash())) {
            u.setIntentosFallidos(0);
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(u, "LOGIN_EXITOSO");
            return u;
        }

        // Contraseña incorrecta: contamos el intento y bloqueamos automáticamente al llegar al máximo
        u.setIntentosFallidos(u.getIntentosFallidos() + 1);
        if (u.getIntentosFallidos() >= MAX_INTENTOS_FALLIDOS) {
            u.setBloqueado(true);
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(u, "BLOQUEO_AUTOMATICO",
                    "Cuenta bloqueada tras " + MAX_INTENTOS_FALLIDOS + " intentos fallidos consecutivos.");
            throw new NegocioException("La cuenta ha sido bloqueada por demasiados intentos fallidos. Contacte al administrador.");
        }
        usuarioRepository.save(u);
        auditoriaAccesoService.registrar(u, "LOGIN_FALLIDO",
                "Intento " + u.getIntentosFallidos() + " de " + MAX_INTENTOS_FALLIDOS + ".");
        throw new NegocioException("Usuario o contraseña incorrectos.");
    }

    @Override
    public void registrarLogout(Usuario usuario) {
        auditoriaAccesoService.registrar(usuario, "LOGOUT");
    }

    @Override
    public Usuario registrarCuenta(String usuario, String contrasena, String nombreCompleto, String email) {
        if (usuarioRepository.existsByNombreUsuarioIgnoreCase(usuario.trim())) {
            throw new NegocioException("El nombre de usuario ya existe.");
        }
        // Las nuevas cuentas nacen con rol 'SIN_ASIGNAR' y estado 'B' (Bloqueado / Pendiente de aprobación)
        Usuario u = new Usuario(usuario.trim(), PasswordUtil.hash(contrasena), "SIN_ASIGNAR", nombreCompleto, email, true);
        return usuarioRepository.save(u);
    }

    @Override
    public Usuario cambiarContrasena(Long idUsuario, String contrasenaActual, String nuevaContrasena) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NegocioException("La contraseña actual no es correcta."));
        if (!PasswordUtil.verificar(contrasenaActual, u.getContrasenaHash())) {
            throw new NegocioException("La contraseña actual no es correcta.");
        }
        u.setContrasenaHash(PasswordUtil.hash(nuevaContrasena));
        return usuarioRepository.save(u);
    }

    @Override
    public void recuperarContrasena(String usuario) {
        Usuario u = usuarioRepository.findByNombreUsuarioIgnoreCase(usuario.trim())
                .orElseThrow(() -> new NegocioException("No se encontró el usuario ingresado."));

        if (u.getEmail() == null || u.getEmail().isBlank()) {
            throw new NegocioException("Este usuario no tiene un correo registrado. Contacte al administrador.");
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
            throw new NegocioException("No se pudo enviar el correo de recuperación. Intente más tarde o contacte al administrador.");
        }

        u.setContrasenaHash(PasswordUtil.hash(contrasenaTemporal));
        u.setIntentosFallidos(0);
        u.setBloqueado(false);
        usuarioRepository.save(u);
        auditoriaAccesoService.registrar(u, "RESET_CONTRASENA", "Contraseña temporal enviada por correo.");
    }

    @Override
    @Transactional(readOnly = true)
    public void recuperarNombreUsuario(String email) {
        Usuario u = usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new NegocioException("No se encontró ninguna cuenta registrada con ese correo electrónico."));

        try {
            emailService.enviarCorreo(u.getEmail(), "NurseLogic Digital - Recuperación de nombre de usuario",
                    "Tu nombre de usuario registrado en NurseLogic Digital es:\n\n"
                            + u.getNombreUsuario()
                            + "\n\nSi olvidaste también tu contraseña, usa la opción \"Olvidé mi Contraseña\" en la pantalla de inicio de sesión.\n"
                            + "Si tú no solicitaste esto, contacta al administrador del sistema.");
        } catch (MailException ex) {
            throw new NegocioException("No se pudo enviar el correo de recuperación. Intente más tarde o contacte al administrador.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTodos() {
        return usuarioRepository.count();
    }

    @Override
    public void cambiarEstado(Usuario actor, Long idUsuario, String nuevoEstado) {
        usuarioRepository.findById(idUsuario).ifPresent(u -> {
            if ("admin".equalsIgnoreCase(u.getNombreUsuario()) || (u.getIdUsuario() != null && u.getIdUsuario() == 1L)) {
                throw new NegocioException("No se permite bloquear ni alterar al Super Administrador principal del sistema.");
            }
            u.setEstado(nuevoEstado);
            u.setBloqueado("B".equalsIgnoreCase(nuevoEstado));
            if (!u.isBloqueado()) {
                u.setIntentosFallidos(0); // desbloqueo manual también reinicia el contador de intentos
            }
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(actor,
                    u.isBloqueado() ? "BLOQUEO_MANUAL_USUARIO" : "DESBLOQUEO_MANUAL_USUARIO",
                    "Usuario afectado: " + u.getNombreUsuario());
        });
    }

    @Override
    public void asignarRol(Usuario actor, Long idUsuario, String nuevoRol) {
        usuarioRepository.findById(idUsuario).ifPresent(u -> {
            if ("admin".equalsIgnoreCase(u.getNombreUsuario()) || (u.getIdUsuario() != null && u.getIdUsuario() == 1L)) {
                throw new NegocioException("No se permite cambiar el rol al Super Administrador principal del sistema.");
            }
            u.setRol(nuevoRol);
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(actor, "ASIGNACION_ROL",
                    "Rol '" + nuevoRol + "' asignado a: " + u.getNombreUsuario());
        });
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
}
