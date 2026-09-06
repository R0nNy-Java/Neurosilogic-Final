package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AuditoriaAccesoRepository;
import com.rrparedes.neurosilogic.repository.CierreFichaRepository;
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
    private final AlertaClinicaRepository alertaClinicaRepository;
    private final CierreFichaRepository cierreFichaRepository;

    public DashboardController(PacienteService pacienteService, UsuarioService usuarioService,
                               AuditoriaAccesoRepository auditoriaAccesoRepository,
                               AlertaClinicaRepository alertaClinicaRepository,
                               CierreFichaRepository cierreFichaRepository) {
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.auditoriaAccesoRepository = auditoriaAccesoRepository;
        this.alertaClinicaRepository = alertaClinicaRepository;
        this.cierreFichaRepository = cierreFichaRepository;
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
    public String reportes(@org.springframework.web.bind.annotation.RequestParam(required = false) String cedula,
                           @org.springframework.web.bind.annotation.RequestParam(required = false) String fechaInicio,
                           @org.springframework.web.bind.annotation.RequestParam(required = false) String fechaFin,
                           HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioLogueado);

        java.time.LocalDateTime startDT = null;
        java.time.LocalDateTime endDT = null;

        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            try {
                startDT = java.time.LocalDate.parse(fechaInicio.trim()).atStartOfDay();
                model.addAttribute("fechaInicioSel", fechaInicio.trim());
            } catch (Exception ignored) {}
        }
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            try {
                endDT = java.time.LocalDate.parse(fechaFin.trim()).atTime(23, 59, 59);
                model.addAttribute("fechaFinSel", fechaFin.trim());
            } catch (Exception ignored) {}
        }

        // Si es administrador, proveer datos de auditoría global y actividad de enfermeros
        if (usuarioLogueado.isAdministrador()) {
            if (startDT != null && endDT != null) {
                model.addAttribute("todosLosCierres", cierreFichaRepository.findByFechaCierreBetweenOrderByFechaCierreDesc(startDT, endDT));
                model.addAttribute("todasLasAlertas", alertaClinicaRepository.findByFechaRegistroBetweenOrderByFechaRegistroDesc(startDT, endDT));
            } else {
                model.addAttribute("todosLosCierres", cierreFichaRepository.findAllByOrderByFechaCierreDesc());
                model.addAttribute("todasLasAlertas", alertaClinicaRepository.findAllByOrderByFechaRegistroDesc());
            }
            model.addAttribute("todosLosMovimientos", auditoriaAccesoRepository.findAllByOrderByTimestampDesc());
            model.addAttribute("listaEnfermeros", usuarioService.listarTodos());
            model.addAttribute("todosLosPacientes", pacienteService.listarTodos());
        } else {
            if (startDT != null && endDT != null) {
                model.addAttribute("misCierresFicha", cierreFichaRepository.findByEnfermeroIdUsuarioAndFechaCierreBetweenOrderByFechaCierreDesc(usuarioLogueado.getIdUsuario(), startDT, endDT));
            } else {
                model.addAttribute("misCierresFicha", pacienteService.obtenerCierresFichaPorEnfermero(usuarioLogueado.getIdUsuario()));
            }
            model.addAttribute("todosLosPacientes", pacienteService.listarTodos());
        }

        if (cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("cedulaBuscada", cedula.trim());
            pacienteService.buscarPorCedula(cedula.trim()).ifPresentOrElse(paciente -> {
                model.addAttribute("pacienteEncontrado", paciente);
                pacienteService.obtenerPanel(paciente.getIdPaciente()).ifPresent(panel -> {
                    model.addAttribute("panelData", panel);
                });
            }, () -> {
                model.addAttribute("errorCedula", "No se encontró ningún paciente registrado con la cédula ingresada.");
            });
        }
        return "reportes";
    }
}
