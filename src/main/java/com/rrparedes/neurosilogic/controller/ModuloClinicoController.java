package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.*;
import com.rrparedes.neurosilogic.service.AlertaClinicaService;
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
    private final DosificacionRepository dosificacionRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final AlertaClinicaService alertaClinicaService;

    public ModuloClinicoController(PacienteRepository pacienteRepository,
                                  SignoVitalRepository signoVitalRepository,
                                  EscalaGlasgowRepository escalaGlasgowRepository,
                                  EvaluacionIMCRepository evaluacionIMCRepository,
                                  AntecedenteRepository antecedenteRepository,
                                  DosificacionRepository dosificacionRepository,
                                  EnfermedadRepository enfermedadRepository,
                                  AlertaClinicaService alertaClinicaService) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.dosificacionRepository = dosificacionRepository;
        this.enfermedadRepository = enfermedadRepository;
        this.alertaClinicaService = alertaClinicaService;
    }

    // ── Signos Vitales ──
    @GetMapping("/signos-vitales")
    public String showSignosVitales(@RequestParam(required = false) Long idPaciente,
                                    @RequestParam(required = false) String cedula,
                                    HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = Optional.empty();
        if (idPaciente != null) {
            pOpt = pacienteRepository.findById(idPaciente);
        } else if (cedula != null && !cedula.trim().isEmpty()) {
            pOpt = pacienteRepository.findByCedula(cedula.trim());
            if (pOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
            }
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", signoVitalRepository.findByIdPaciente(p.getIdPaciente()));
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

        // Validación estricta backend: Impedir valores negativos o fuera de rango físico
        if (presionSistolica <= 0 || presionDiastolica <= 0 || frecuenciaCardiaca <= 0 ||
            frecuenciaRespiratoria <= 0 || temperatura <= 0 || saturacionOxigeno <= 0 || saturacionOxigeno > 100) {
            return "redirect:/signos-vitales?idPaciente=" + idPaciente;
        }

        SignoVital sv = new SignoVital();
        sv.setIdPaciente(idPaciente);
        sv.setPresionArterial(presionSistolica + "/" + presionDiastolica);
        sv.setFrecuenciaCardiaca(frecuenciaCardiaca);
        sv.setFrecuenciaRespiratoria(frecuenciaRespiratoria);
        sv.setTemperatura(temperatura);
        sv.setSaturacionOxigeno(saturacionOxigeno);

        boolean hayAlerta = alertaClinicaService.evaluarSignosVitales(idPaciente, sv);
        sv.setAlertaGenerada(hayAlerta ? "S" : "N");
        signoVitalRepository.save(sv);
        return "redirect:/signos-vitales?idPaciente=" + idPaciente;
    }

    // ── Escala de Glasgow ──
    @GetMapping("/escala-glasgow")
    public String showGlasgow(@RequestParam(required = false) Long idPaciente,
                              @RequestParam(required = false) String cedula,
                              HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = Optional.empty();
        if (idPaciente != null) {
            pOpt = pacienteRepository.findById(idPaciente);
        } else if (cedula != null && !cedula.trim().isEmpty()) {
            pOpt = pacienteRepository.findByCedula(cedula.trim());
            if (pOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
            }
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", escalaGlasgowRepository.findByIdPaciente(p.getIdPaciente()));
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

        EscalaGlasgow eg = new EscalaGlasgow();
        eg.setIdPaciente(idPaciente);
        eg.setRespuestaOcular(respuestaOcular);
        eg.setRespuestaVerbal(respuestaVerbal);
        eg.setRespuestaMotora(respuestaMotora);

        alertaClinicaService.evaluarGlasgow(idPaciente, eg);
        escalaGlasgowRepository.save(eg);
        return "redirect:/escala-glasgow?idPaciente=" + idPaciente;
    }

    // ── Evaluación IMC ──
    @GetMapping("/evaluacion-imc")
    public String showIMC(@RequestParam(required = false) Long idPaciente,
                          @RequestParam(required = false) String cedula,
                          HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = Optional.empty();
        if (idPaciente != null) {
            pOpt = pacienteRepository.findById(idPaciente);
        } else if (cedula != null && !cedula.trim().isEmpty()) {
            pOpt = pacienteRepository.findByCedula(cedula.trim());
            if (pOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
            }
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", evaluacionIMCRepository.findByIdPaciente(p.getIdPaciente()));
        }
        return "evaluacion_imc";
    }

    @PostMapping("/evaluacion-imc/guardar")
    public String registrarIMC(@RequestParam Long idPaciente,
                               @RequestParam Double pesoKg,
                               @RequestParam Double estaturaM,
                               HttpSession session) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        // Validación estricta backend: Impedir números negativos o improbables
        if (pesoKg == null || pesoKg <= 0 || estaturaM == null || estaturaM <= 0) {
            return "redirect:/evaluacion-imc?idPaciente=" + idPaciente;
        }

        EvaluacionIMC imc = new EvaluacionIMC();
        imc.setIdPaciente(idPaciente);
        imc.setPesoKg(pesoKg);
        imc.setEstaturaM(estaturaM);

        alertaClinicaService.evaluarIMC(idPaciente, imc);
        evaluacionIMCRepository.save(imc);
        return "redirect:/evaluacion-imc?idPaciente=" + idPaciente;
    }

    // ── Antecedentes Médicos ──
    @GetMapping("/antecedentes")
    public String showAntecedentes(@RequestParam Long idPaciente, HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";
        Optional<Paciente> pOpt = pacienteRepository.findById(idPaciente);
        if (pOpt.isPresent()) {
            model.addAttribute("paciente", pOpt.get());
            model.addAttribute("historial", antecedenteRepository.findByIdPaciente(idPaciente));
            model.addAttribute("enfermedades", enfermedadRepository.findAll());
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

        Antecedente a = new Antecedente();
        a.setIdPaciente(idPaciente);
        a.setTipo(tipo);

        if ("Patológico".equalsIgnoreCase(tipo) && enfermedadSeleccionada != null && !enfermedadSeleccionada.trim().isEmpty()) {
            a.setObservacion(enfermedadSeleccionada.trim() + " - " + descripcion);
        } else if ("Alergia".equalsIgnoreCase(tipo)) {
            a.setAlergias(descripcion);
            a.setObservacion("ALERGIA REGISTRADA: " + descripcion);
        } else {
            a.setObservacion(descripcion);
        }

        antecedenteRepository.save(a);
        return "redirect:/antecedentes?idPaciente=" + idPaciente;
    }

    // ── Dosificación ──
    @GetMapping("/dosificacion")
    public String showDosificacion(@RequestParam(required = false) Long idPaciente,
                                   @RequestParam(required = false) String cedula,
                                   HttpSession session, Model model) {
        if (session.getAttribute("usuarioLogueado") == null) return "redirect:/login";

        Optional<Paciente> pOpt = Optional.empty();
        if (idPaciente != null) {
            pOpt = pacienteRepository.findById(idPaciente);
        } else if (cedula != null && !cedula.trim().isEmpty()) {
            pOpt = pacienteRepository.findByCedula(cedula.trim());
            if (pOpt.isEmpty()) {
                model.addAttribute("error", "No se encontró ningún paciente registrado con la cédula " + cedula);
            }
        }

        if (pOpt.isPresent()) {
            Paciente p = pOpt.get();
            model.addAttribute("paciente", p);
            model.addAttribute("historial", dosificacionRepository.findByIdPacienteOrderByFechaRegistroDesc(p.getIdPaciente()));
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

        Dosificacion d = new Dosificacion();
        d.setIdPaciente(idPaciente);
        d.setMedicamento(medicamento);
        d.setUnidadDosis(unidadDosis);
        d.setUnidadPresentacion(unidadPresentacion);
        d.setPresentacion(presentacion);
        d.setDiluyenteMl(diluyenteMl);
        d.setHorasTotales(horasTotales);
        d.setDosisIndicada(dosisIndicada);

        dosificacionRepository.save(d);
        return "redirect:/dosificacion?idPaciente=" + idPaciente;
    }
}
