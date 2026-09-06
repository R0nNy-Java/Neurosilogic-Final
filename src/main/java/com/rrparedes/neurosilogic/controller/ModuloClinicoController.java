package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.service.DosificacionService;
import com.rrparedes.neurosilogic.service.ModuloClinicoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ModuloClinicoController {

    private final ModuloClinicoService moduloClinicoService;
    private final DosificacionService dosificacionService;

    public ModuloClinicoController(ModuloClinicoService moduloClinicoService, DosificacionService dosificacionService) {
        this.moduloClinicoService = moduloClinicoService;
        this.dosificacionService = dosificacionService;
    }

    // ── Signos Vitales ──
    @GetMapping("/signos-vitales")
    public String showSignosVitales(@RequestParam(required = false) Long idPaciente,
                                    @RequestParam(required = false) String cedula,
                                    HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = moduloClinicoService.buscarPaciente(idPaciente, cedula);
        if (pOpt.isEmpty() && cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", moduloClinicoService.historialSignosVitales(p.getIdPaciente()));
        }
        return "signos_vitales";
    }

    @PostMapping("/signos-vitales/guardar")
    public String registrarSignosVitales(@RequestParam Long idPaciente,
                                         @RequestParam Integer presionSistolica,
                                         @RequestParam Integer presionDiastolica,
                                         @RequestParam Integer frecuenciaCardiaca,
                                         @RequestParam Integer frecuenciaRespiratoria,
                                         @RequestParam Double temperatura,
                                         @RequestParam Integer saturacionOxigeno,
                                         HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        moduloClinicoService.registrarSignoVital(idPaciente, presionSistolica, presionDiastolica,
                frecuenciaCardiaca, frecuenciaRespiratoria, temperatura, saturacionOxigeno);
        return "redirect:/signos-vitales?idPaciente=" + idPaciente;
    }

    // ── Escala de Glasgow ──
    @GetMapping("/escala-glasgow")
    public String showGlasgow(@RequestParam(required = false) Long idPaciente,
                              @RequestParam(required = false) String cedula,
                              HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = moduloClinicoService.buscarPaciente(idPaciente, cedula);
        if (pOpt.isEmpty() && cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", moduloClinicoService.historialGlasgow(p.getIdPaciente()));
        }
        return "escala_glasgow";
    }

    @PostMapping("/escala-glasgow/guardar")
    public String registrarGlasgow(@RequestParam Long idPaciente,
                                   @RequestParam Integer respuestaOcular,
                                   @RequestParam Integer respuestaVerbal,
                                   @RequestParam Integer respuestaMotora,
                                   HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        moduloClinicoService.registrarGlasgow(idPaciente, respuestaOcular, respuestaVerbal, respuestaMotora);
        return "redirect:/escala-glasgow?idPaciente=" + idPaciente;
    }

    // ── Evaluación IMC ──
    @GetMapping("/evaluacion-imc")
    public String showIMC(@RequestParam(required = false) Long idPaciente,
                          @RequestParam(required = false) String cedula,
                          HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = moduloClinicoService.buscarPaciente(idPaciente, cedula);
        if (pOpt.isEmpty() && cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", moduloClinicoService.historialIMC(p.getIdPaciente()));
        }
        return "evaluacion_imc";
    }

    @PostMapping("/evaluacion-imc/guardar")
    public String registrarIMC(@RequestParam Long idPaciente,
                               @RequestParam Double pesoKg,
                               @RequestParam Double estaturaM,
                               HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        moduloClinicoService.registrarIMC(idPaciente, pesoKg, estaturaM);
        return "redirect:/evaluacion-imc?idPaciente=" + idPaciente;
    }

    // ── Antecedentes Médicos ──
    @GetMapping("/antecedentes")
    public String showAntecedentes(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = moduloClinicoService.buscarPaciente(idPaciente, null);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", moduloClinicoService.historialAntecedentes(idPaciente));
            model.addAttribute("enfermedades", moduloClinicoService.listarEnfermedades());
            return "antecedentes";
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/antecedentes/guardar")
    public String registrarAntecedente(@RequestParam Long idPaciente,
                                       @RequestParam String tipo,
                                       @RequestParam(required = false) String enfermedadSeleccionada,
                                       @RequestParam String descripcion,
                                       HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        moduloClinicoService.registrarAntecedente(idPaciente, tipo, enfermedadSeleccionada, descripcion);
        return "redirect:/antecedentes?idPaciente=" + idPaciente;
    }

    // ── Dosificación ──
    @GetMapping("/dosificacion")
    public String showDosificacion(@RequestParam(required = false) Long idPaciente,
                                   @RequestParam(required = false) String cedula,
                                   HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = dosificacionService.buscarPaciente(idPaciente, cedula);
        if (pOpt.isEmpty() && cedula != null && !cedula.trim().isEmpty()) {
            model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", dosificacionService.historial(p.getIdPaciente()));
        }
        return "dosificacion";
    }

    @PostMapping("/dosificacion/guardar")
    public String registrarDosificacion(@RequestParam Long idPaciente,
                                        @RequestParam String medicamento,
                                        @RequestParam Double dosisIndicada,
                                        @RequestParam String unidadDosis,
                                        @RequestParam Double presentacion,
                                        @RequestParam String unidadPresentacion,
                                        @RequestParam Double diluyenteMl,
                                        @RequestParam(required = false) Double horasTotales,
                                        HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        dosificacionService.registrar(idPaciente, medicamento, dosisIndicada, unidadDosis,
                presentacion, unidadPresentacion, diluyenteMl, horasTotales);
        return "redirect:/dosificacion?idPaciente=" + idPaciente;
    }
}
