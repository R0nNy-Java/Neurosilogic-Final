package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.MedicamentoRepository;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.service.AuditoriaAccesoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final AuditoriaAccesoService auditoriaAccesoService;

    public AdminController(UsuarioRepository usuarioRepository, MedicamentoRepository medicamentoRepository,
                           AuditoriaAccesoService auditoriaAccesoService) {
        this.usuarioRepository = usuarioRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.auditoriaAccesoService = auditoriaAccesoService;
    }

    // Verifica sesión activa y rol de administrador; retorna la ruta de redirección si el acceso
    // debe rechazarse, o null si el usuario puede continuar. Centraliza el chequeo que antes
    // estaba duplicado en cada endpoint de este controlador.
    private String verificarAcceso(HttpSession session) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";
        if (!userLog.isAdministrador()) return "redirect:/dashboard";
        return null;
    }

    // ── Gestión de Usuarios ──
    @GetMapping("/gestionar-usuarios")
    public String gestionarUsuarios(HttpSession session, Model model) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;

        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "gestionar_usuarios";
    }

    @PostMapping("/gestionar-usuarios/bloquear")
    public String cambiarEstadoUsuario(@RequestParam Long idUsuario, @RequestParam String nuevoEstado, HttpSession session) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        Optional<Usuario> uOpt = usuarioRepository.findById(idUsuario);
        if (uOpt.isPresent()) {
            Usuario u = uOpt.get();
            u.setEstado(nuevoEstado);
            u.setBloqueado("B".equalsIgnoreCase(nuevoEstado));
            if (!u.isBloqueado()) {
                u.setIntentosFallidos(0); // desbloqueo manual también reinicia el contador de intentos
            }
            usuarioRepository.save(u);
            auditoriaAccesoService.registrar(userLog,
                    u.isBloqueado() ? "BLOQUEO_MANUAL_USUARIO" : "DESBLOQUEO_MANUAL_USUARIO",
                    "Usuario afectado: " + u.getNombreUsuario());
        }
        return "redirect:/gestionar-usuarios";
    }

    // ── Gestión de Catálogo de Medicamentos ──
    @GetMapping("/gestionar-catalogo")
    public String gestionarCatalogo(HttpSession session, Model model) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;

        model.addAttribute("medicamentos", medicamentoRepository.findAll());
        return "gestionar_catalogo";
    }

    @PostMapping("/gestionar-catalogo/guardar")
    public String guardarMedicamento(@RequestParam String nombre,
                                     @RequestParam String composicion,
                                     @RequestParam String dosisRecomendada,
                                     HttpSession session) {
        String rechazo = verificarAcceso(session);
        if (rechazo != null) return rechazo;
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");

        Medicamento med = new Medicamento(nombre, composicion, dosisRecomendada);
        medicamentoRepository.save(med);
        auditoriaAccesoService.registrar(userLog, "ALTA_MEDICAMENTO_CATALOGO", "Medicamento: " + nombre);
        return "redirect:/gestionar-catalogo";
    }
}
