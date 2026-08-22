package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class ModuloClinicoController {

    private final PacienteRepository pacienteRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final EscalaGlasgowRepository escalaGlasgowRepository;
    private final EvaluacionIMCRepository evaluacionIMCRepository;
    private final AntecedenteRepository antecedenteRepository;

    public ModuloClinicoController(PacienteRepository pacienteRepository,
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

    // ── Signos Vitales ──
    @GetMapping("/signos-vitales")
    public String showSignosVitales(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(idPaciente);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", signoVitalRepository.findByIdPaciente(idPaciente));
            return "signos_vitales";
        }
        return "redirect:/pacientes";
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

        SignoVital sv = new SignoVital();
        sv.setIdPaciente(idPaciente);
        sv.setPresionArterial(presionSistolica + "/" + presionDiastolica);
        sv.setFrecuenciaCardiaca(frecuenciaCardiaca);
        sv.setFrecuenciaRespiratoria(frecuenciaRespiratoria);
        sv.setTemperatura(temperatura);
        sv.setSaturacionOxigeno(saturacionOxigeno);

        signoVitalRepository.save(sv);
        return "redirect:/paciente/panel?id=" + idPaciente;
    }

    // ── Escala de Glasgow ──
    @GetMapping("/escala-glasgow")
    public String showGlasgow(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(idPaciente);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", escalaGlasgowRepository.findByIdPaciente(idPaciente));
            return "escala_glasgow";
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/escala-glasgow/guardar")
    public String registrarGlasgow(@RequestParam Long idPaciente,
                                   @RequestParam Integer respuestaOcular,
                                   @RequestParam Integer respuestaVerbal,
                                   @RequestParam Integer respuestaMotora,
                                   HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        EscalaGlasgow eg = new EscalaGlasgow();
        eg.setIdPaciente(idPaciente);
        eg.setRespuestaOcular(respuestaOcular);
        eg.setRespuestaVerbal(respuestaVerbal);
        eg.setRespuestaMotora(respuestaMotora);

        escalaGlasgowRepository.save(eg);
        return "redirect:/paciente/panel?id=" + idPaciente;
    }

    // ── Evaluación IMC ──
    @GetMapping("/evaluacion-imc")
    public String showIMC(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(idPaciente);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", evaluacionIMCRepository.findByIdPaciente(idPaciente));
            return "evaluacion_imc";
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/evaluacion-imc/guardar")
    public String registrarIMC(@RequestParam Long idPaciente,
                               @RequestParam Double pesoKg,
                               @RequestParam Double estaturaM,
                               HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        EvaluacionIMC imc = new EvaluacionIMC();
        imc.setIdPaciente(idPaciente);
        imc.setPesoKg(pesoKg);
        imc.setEstaturaM(estaturaM);

        evaluacionIMCRepository.save(imc);
        return "redirect:/paciente/panel?id=" + idPaciente;
    }

    // ── Antecedentes Médicos ──
    @GetMapping("/antecedentes")
    public String showAntecedentes(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(idPaciente);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", antecedenteRepository.findByIdPaciente(idPaciente));
            return "antecedentes";
        }
        return "redirect:/pacientes";
    }

    @PostMapping("/antecedentes/guardar")
    public String registrarAntecedente(@RequestParam Long idPaciente,
                                       @RequestParam String tipo,
                                       @RequestParam String descripcion,
                                       HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Antecedente a = new Antecedente();
        a.setIdPaciente(idPaciente);
        a.setTipo(tipo);
        a.setDescripcion(descripcion);

        antecedenteRepository.save(a);
        return "redirect:/paciente/panel?id=" + idPaciente;
    }

    // ── Dosificación ──
    @GetMapping("/dosificacion")
    public String showDosificacion(HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        return "dosificacion";
    }
}
