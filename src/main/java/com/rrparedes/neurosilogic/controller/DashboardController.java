package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import com.rrparedes.neurosilogic.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public DashboardController(PacienteRepository pacienteRepository, UsuarioRepository usuarioRepository) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("totalPacientes", pacienteRepository.count());
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "dashboard";
    }
}
