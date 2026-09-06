package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.AuditoriaAccesoRepository;
import com.rrparedes.neurosilogic.service.PacienteService;
import com.rrparedes.neurosilogic.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final AuditoriaAccesoRepository auditoriaAccesoRepository;

    public DashboardController(PacienteService pacienteService, UsuarioService usuarioService, AuditoriaAccesoRepository auditoriaAccesoRepository) {
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.auditoriaAccesoRepository = auditoriaAccesoRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioLogueado);
        model.addAttribute("totalPacientes", pacienteService.contarTodos());
        model.addAttribute("totalUsuarios", usuarioService.contarTodos());
        model.addAttribute("pacientes", pacienteService.obtenerPacientesEnEvaluacionPorEnfermero(usuarioLogueado.getIdUsuario()));
        model.addAttribute("cierresFicha", pacienteService.obtenerCierresFichaPorEnfermero(usuarioLogueado.getIdUsuario()));
        model.addAttribute("ultimosMovimientos", auditoriaAccesoRepository.findAllByOrderByTimestampDesc());
        return "dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioLogueado);
        return "reportes";
    }
}
