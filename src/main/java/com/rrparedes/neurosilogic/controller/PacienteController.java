package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.service.NegocioException;
import com.rrparedes.neurosilogic.service.PacienteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/pacientes")
    public String listarPacientes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        model.addAttribute("pacientes", pacienteService.listarTodos());
        return "pacientes";
    }

    @GetMapping("/pacientes/nuevo")
    public String nuevoPacienteForm(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        model.addAttribute("paciente", new Paciente());
        return "registro_paciente";
    }

    @PostMapping("/pacientes/guardar")
    public String guardarPaciente(@Valid @ModelAttribute Paciente paciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        try {
            pacienteService.registrar(paciente);
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("paciente", paciente);
            return "registro_paciente";
        }
        return "redirect:/pacientes";
    }

    @GetMapping("/paciente/panel")
    public String panelPaciente(@RequestParam Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        return pacienteService.obtenerPanel(id).map(panel -> {
            model.addAttribute("paciente", panel.paciente());
            model.addAttribute("signosVitales", panel.signosVitales());
            model.addAttribute("glasgowList", panel.glasgowList());
            model.addAttribute("imcList", panel.imcList());
            model.addAttribute("antecedentesList", panel.antecedentesList());
            model.addAttribute("alertas", panel.alertas());
            return "panel_paciente";
        }).orElse("redirect:/pacientes");
    }
}
