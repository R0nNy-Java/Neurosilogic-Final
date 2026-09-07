package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.service.NegocioException;
import com.rrparedes.neurosilogic.service.PacienteService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            Paciente guardado = pacienteService.registrar(paciente);
            return "redirect:/paciente/panel?id=" + guardado.getIdPaciente();
        } catch (NegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("paciente", paciente);
            return "registro_paciente";
        }
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

            boolean tieneSV = !panel.signosVitales().isEmpty();
            boolean tieneGlasgow = !panel.glasgowList().isEmpty();
            boolean tieneIMC = !panel.imcList().isEmpty();
            boolean puedeCerrar = tieneSV && tieneGlasgow && tieneIMC;
            boolean tieneAlertasActivas = !panel.alertas().isEmpty();

            model.addAttribute("tieneSV", tieneSV);
            model.addAttribute("tieneGlasgow", tieneGlasgow);
            model.addAttribute("tieneIMC", tieneIMC);
            model.addAttribute("puedeCerrarFicha", puedeCerrar);
            model.addAttribute("tieneAlertasActivas", tieneAlertasActivas);

            return "panel_paciente";
        }).orElse("redirect:/pacientes");
    }

    @PostMapping("/paciente/cerrar-ficha")
    public String cerrarFicha(@RequestParam Long idPaciente, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario enfermero = (Usuario) session.getAttribute("usuarioLogueado");
        if (enfermero == null) return "redirect:/login";
        try {
            pacienteService.registrarCierreFicha(idPaciente, enfermero);
            redirectAttributes.addFlashAttribute("mensajeExito", "Información de Cierre de Ficha guardada exitosamente en la base de datos.");
            return "redirect:/pacientes";
        } catch (NegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/paciente/panel?id=" + idPaciente;
        }
    }

    @PostMapping("/paciente/dar-alta")
    public String darAlta(@RequestParam Long idPaciente, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario enfermero = (Usuario) session.getAttribute("usuarioLogueado");
        if (enfermero == null) return "redirect:/login";
        try {
            pacienteService.darDeAlta(idPaciente, enfermero);
            redirectAttributes.addFlashAttribute("mensajeExito", "Paciente dado de alta exitosamente por estar estable (0 alertas activas). Ficha marcada como Inactiva.");
            return "redirect:/pacientes";
        } catch (NegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/paciente/panel?id=" + idPaciente;
        }
    }

    @PostMapping("/paciente/activar-ficha")
    public String activarFicha(@RequestParam Long idPaciente, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario enfermero = (Usuario) session.getAttribute("usuarioLogueado");
        if (enfermero == null) return "redirect:/login";
        try {
            pacienteService.activarFicha(idPaciente, enfermero);
            redirectAttributes.addFlashAttribute("mensajeExito", "Ficha del paciente reactivada exitosamente por re-ingreso.");
            return "redirect:/paciente/panel?id=" + idPaciente;
        } catch (NegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
            return "redirect:/paciente/panel?id=" + idPaciente;
        }
    }
}
