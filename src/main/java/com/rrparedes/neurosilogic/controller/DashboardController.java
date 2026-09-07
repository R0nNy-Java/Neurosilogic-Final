package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.CierreFicha;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.service.PacienteService;
import com.rrparedes.neurosilogic.service.ReporteResumenPeriodo;
import com.rrparedes.neurosilogic.service.ReporteService;
import com.rrparedes.neurosilogic.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final ReporteService reporteService;

    public DashboardController(PacienteService pacienteService, UsuarioService usuarioService, ReporteService reporteService) {
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
        this.reporteService = reporteService;
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
        model.addAttribute("ultimosMovimientos", reporteService.obtenerUltimosMovimientos());
        return "dashboard";
    }

    @GetMapping("/reportes")
    public String reportes(@RequestParam(required = false) String cedula,
                           @RequestParam(required = false) String fechaInicio,
                           @RequestParam(required = false) String fechaFin,
                           HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", usuarioLogueado);

        LocalDateTime startDT = null;
        LocalDateTime endDT = null;

        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            try {
                startDT = LocalDate.parse(fechaInicio.trim()).atStartOfDay();
                model.addAttribute("fechaInicioSel", fechaInicio.trim());
            } catch (Exception ignored) {}
        }
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            try {
                endDT = LocalDate.parse(fechaFin.trim()).atTime(23, 59, 59);
                model.addAttribute("fechaFinSel", fechaFin.trim());
            } catch (Exception ignored) {}
        }

        ReporteResumenPeriodo datos = reporteService.generarReportePeriodo(usuarioLogueado, startDT, endDT);

        List<CierreFicha> misCierresFicha = usuarioLogueado.isAdministrador() ? List.of() : datos.misCierresFicha();

        if (usuarioLogueado.isAdministrador()) {
            model.addAttribute("todosLosCierres", datos.todosLosCierres());
            model.addAttribute("todasLasAlertas", datos.todasLasAlertas());
            model.addAttribute("todosLosMovimientos", datos.todosLosMovimientos());
            model.addAttribute("listaEnfermeros", datos.listaEnfermeros());
        } else {
            model.addAttribute("misCierresFicha", misCierresFicha);
        }
        model.addAttribute("todosLosPacientes", datos.todosLosPacientes());
        model.addAttribute("decision", datos.decision());

        if (cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("cedulaBuscada", cedula.trim());
            pacienteService.buscarPorCedula(cedula.trim()).ifPresentOrElse(paciente -> {
                model.addAttribute("pacienteEncontrado", paciente);
                pacienteService.obtenerPanel(paciente.getIdPaciente()).ifPresent(panel -> {
                    model.addAttribute("panelData", panel);
                });
                // Se filtra aquí (no con OGNL en la plantilla) para evitar evaluar una selección
                // sobre una variable de contexto ("pacienteEncontrado") desde dentro del propio
                // filtro, que Thymeleaf no resuelve y corta la respuesta a mitad de camino.
                List<CierreFicha> cierresDelPaciente = misCierresFicha.stream()
                        .filter(cf -> cf.getPaciente() != null
                                && cf.getPaciente().getIdPaciente().equals(paciente.getIdPaciente()))
                        .collect(Collectors.toList());
                model.addAttribute("cierresPacienteEncontrado", cierresDelPaciente);
            }, () -> {
                model.addAttribute("errorCedula", "No se encontró ningún paciente registrado con la cédula ingresada.");
            });
        }
        return "reportes";
    }
}
