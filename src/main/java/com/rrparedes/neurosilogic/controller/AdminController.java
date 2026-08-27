package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.MedicamentoRepository;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import com.rrparedes.neurosilogic.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final MedicamentoRepository medicamentoRepository;

    public AdminController(UsuarioRepository usuarioRepository, MedicamentoRepository medicamentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.medicamentoRepository = medicamentoRepository;
    }

    // ── Gestión de Usuarios ──
    @GetMapping("/gestionar-usuarios")
    public String gestionarUsuarios(HttpSession session, Model model) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";
        if (!"ADMINISTRADOR".equalsIgnoreCase(userLog.getRol()) && !"ADMIN".equalsIgnoreCase(userLog.getRol())) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "gestionar_usuarios";
    }

    @PostMapping("/gestionar-usuarios/bloquear")
    public String cambiarEstadoUsuario(@RequestParam Long idUsuario, @RequestParam String nuevoEstado, HttpSession session) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";
        if (!"ADMINISTRADOR".equalsIgnoreCase(userLog.getRol()) && !"ADMIN".equalsIgnoreCase(userLog.getRol())) {
            return "redirect:/dashboard";
        }

        Optional<Usuario> uOpt = usuarioRepository.findById(idUsuario);
        if (uOpt.isPresent()) {
            Usuario u = uOpt.get();
            u.setEstado(nuevoEstado);
            u.setBloqueado("B".equalsIgnoreCase(nuevoEstado));
            usuarioRepository.save(u);
        }
        return "redirect:/gestionar-usuarios";
    }

    // ── Gestión de Catálogo de Medicamentos ──
    @GetMapping("/gestionar-catalogo")
    public String gestionarCatalogo(HttpSession session, Model model) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";

        model.addAttribute("medicamentos", medicamentoRepository.findAll());
        return "gestionar_catalogo";
    }

    @PostMapping("/gestionar-catalogo/guardar")
    public String guardarMedicamento(@RequestParam String nombre,
                                     @RequestParam String composicion,
                                     @RequestParam String dosisRecomendada,
                                     HttpSession session) {
        Usuario userLog = (Usuario) session.getAttribute("usuarioLogueado");
        if (userLog == null) return "redirect:/login";

        Medicamento med = new Medicamento(nombre, composicion, dosisRecomendada);
        medicamentoRepository.save(med);
        return "redirect:/gestionar-catalogo";
    }
}
