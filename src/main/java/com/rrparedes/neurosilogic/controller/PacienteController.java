package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final EscalaGlasgowRepository escalaGlasgowRepository;
    private final EvaluacionIMCRepository evaluacionIMCRepository;
    private final AntecedenteRepository antecedenteRepository;

    public PacienteController(PacienteRepository pacienteRepository,
                              SignoVitalRepository signoVitalRepository,
                              EscalaGlasgowRepository escalaGlasgowRepository,
                              EvaluacionIMCRepository evaluacionIMCRepository,
                              AntecedenteRepository antecedenteRepository) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
    }

    @GetMapping("/pacientes")
    public String listarPacientes(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "pacientes";
    }

    @GetMapping("/pacientes/nuevo")
    public String nuevoPacienteForm(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        model.addAttribute("paciente", new Paciente());
        return "registro_paciente";
    }

    @PostMapping("/pacientes/guardar")
    public String guardarPaciente(@ModelAttribute Paciente paciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        if (paciente.getIdPaciente() == null && pacienteRepository.existsByCedula(paciente.getCedula())) {
            model.addAttribute("error", "Ya existe un paciente registrado con la cédula ingresada.");
            model.addAttribute("paciente", paciente);
            return "registro_paciente";
        }
        paciente.setEstado("A");
        pacienteRepository.save(paciente);
        return "redirect:/pacientes";
    }

    @GetMapping("/paciente/panel")
    public String panelPaciente(@RequestParam Long id, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(id);
        if (pOpt.isPresent()) {
            Paciente paciente = pOpt.get();
            model.addAttribute("paciente", paciente);
            model.addAttribute("signosVitales", signoVitalRepository.findByIdPaciente(id));
            model.addAttribute("glasgowList", escalaGlasgowRepository.findByIdPaciente(id));
            model.addAttribute("imcList", evaluacionIMCRepository.findByIdPaciente(id));
            model.addAttribute("antecedentesList", antecedenteRepository.findByIdPaciente(id));
            return "panel_paciente";
        }
        return "redirect:/pacientes";
    }
}
