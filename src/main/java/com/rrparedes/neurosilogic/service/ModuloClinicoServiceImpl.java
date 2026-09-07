package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Antecedente;
import com.rrparedes.neurosilogic.model.EscalaGlasgow;
import com.rrparedes.neurosilogic.model.Enfermedad;
import com.rrparedes.neurosilogic.model.EvaluacionIMC;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.SignoVital;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
import com.rrparedes.neurosilogic.repository.EnfermedadRepository;
import com.rrparedes.neurosilogic.repository.EscalaGlasgowRepository;
import com.rrparedes.neurosilogic.repository.EvaluacionIMCRepository;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import com.rrparedes.neurosilogic.repository.SignoVitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModuloClinicoServiceImpl implements ModuloClinicoService {

    private final PacienteRepository pacienteRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final EscalaGlasgowRepository escalaGlasgowRepository;
    private final EvaluacionIMCRepository evaluacionIMCRepository;
    private final AntecedenteRepository antecedenteRepository;
    private final EnfermedadRepository enfermedadRepository;
    private final AlertaClinicaService alertaClinicaService;

    public ModuloClinicoServiceImpl(PacienteRepository pacienteRepository,
                                    SignoVitalRepository signoVitalRepository,
                                    EscalaGlasgowRepository escalaGlasgowRepository,
                                    EvaluacionIMCRepository evaluacionIMCRepository,
                                    AntecedenteRepository antecedenteRepository,
                                    EnfermedadRepository enfermedadRepository,
                                    AlertaClinicaService alertaClinicaService) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.enfermedadRepository = enfermedadRepository;
        this.alertaClinicaService = alertaClinicaService;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPaciente(Long idPaciente, String cedula) {
        if (idPaciente != null) {
            return pacienteRepository.findById(idPaciente);
        }
        if (cedula != null && !cedula.trim().isEmpty()) {
            return pacienteRepository.findByCedula(cedula.trim());
        }
        return Optional.empty();
    }

    // ── Signos Vitales ──

    @Override
    @Transactional(readOnly = true)
    public List<SignoVital> historialSignosVitales(Long idPaciente) {
        return signoVitalRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    public void registrarSignoVital(Long idPaciente, Integer presionSistolica, Integer presionDiastolica,
                                    Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
                                    Double temperatura, Integer saturacionOxigeno) {
        // Validación estricta backend: impedir valores negativos o fuera de rango físico
        if (presionSistolica <= 0 || presionDiastolica <= 0 || frecuenciaCardiaca <= 0 ||
                frecuenciaRespiratoria <= 0 || temperatura <= 0 || saturacionOxigeno <= 0 || saturacionOxigeno > 100) {
            return;
        }

        Paciente paciente = obtenerPacienteOrThrow(idPaciente);

        SignoVital sv = new SignoVital();
        sv.setPaciente(paciente);
        sv.setPresionArterial(presionSistolica + "/" + presionDiastolica);
        sv.setFrecuenciaCardiaca(frecuenciaCardiaca);
        sv.setFrecuenciaRespiratoria(frecuenciaRespiratoria);
        sv.setTemperatura(temperatura);
        sv.setSaturacionOxigeno(saturacionOxigeno);

        boolean hayAlerta = alertaClinicaService.evaluarSignosVitales(paciente, sv);
        sv.setAlertaGenerada(hayAlerta ? "S" : "N");
        signoVitalRepository.save(sv);
    }

    // ── Escala de Glasgow ──

    @Override
    @Transactional(readOnly = true)
    public List<EscalaGlasgow> historialGlasgow(Long idPaciente) {
        return escalaGlasgowRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    public EscalaGlasgow registrarGlasgow(Long idPaciente, Integer respuestaOcular, Integer respuestaVerbal, Integer respuestaMotora) {
        Paciente paciente = obtenerPacienteOrThrow(idPaciente);

        EscalaGlasgow eg = new EscalaGlasgow();
        eg.setPaciente(paciente);
        eg.setRespuestaOcular(respuestaOcular);
        eg.setRespuestaVerbal(respuestaVerbal);
        eg.setRespuestaMotora(respuestaMotora);

        alertaClinicaService.evaluarGlasgow(paciente, eg);
        return escalaGlasgowRepository.save(eg);
    }

    // ── Evaluación IMC ──

    @Override
    @Transactional(readOnly = true)
    public List<EvaluacionIMC> historialIMC(Long idPaciente) {
        return evaluacionIMCRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    public void registrarIMC(Long idPaciente, Double pesoKg, Double estaturaM) {
        // Validación estricta backend: impedir números negativos o improbables
        if (pesoKg == null || pesoKg <= 0 || estaturaM == null || estaturaM <= 0) {
            return;
        }

        Paciente paciente = obtenerPacienteOrThrow(idPaciente);

        EvaluacionIMC imc = new EvaluacionIMC();
        imc.setPaciente(paciente);
        imc.setPesoKg(pesoKg);
        imc.setEstaturaM(estaturaM);

        alertaClinicaService.evaluarIMC(paciente, imc);
        evaluacionIMCRepository.save(imc);
    }

    // ── Antecedentes Médicos ──

    @Override
    @Transactional(readOnly = true)
    public List<Antecedente> historialAntecedentes(Long idPaciente) {
        return antecedenteRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enfermedad> listarEnfermedades() {
        return enfermedadRepository.findAll();
    }

    @Override
    public Antecedente registrarAntecedente(Long idPaciente, String tipo, String enfermedadSeleccionada, String descripcion) {
        Paciente paciente = obtenerPacienteOrThrow(idPaciente);

        Antecedente a = new Antecedente();
        a.setPaciente(paciente);
        a.setTipo(tipo);

        if ("Patológico".equalsIgnoreCase(tipo) && enfermedadSeleccionada != null && !enfermedadSeleccionada.trim().isEmpty()) {
            // Relación real a la enfermedad inscrita, en vez de concatenarla dentro del texto libre
            // (así el emparejamiento con RangoSignoNormal en AlertaClinicaService sí funciona).
            enfermedadRepository.findByNombreEnfermedadIgnoreCase(enfermedadSeleccionada.trim())
                    .ifPresent(a::setEnfermedad);
            a.setObservacion(descripcion);
        } else if ("Alergia".equalsIgnoreCase(tipo)) {
            a.setAlergias(descripcion);
            a.setObservacion("ALERGIA REGISTRADA: " + descripcion);
        } else {
            a.setObservacion(descripcion);
        }

        return antecedenteRepository.save(a);
    }

    private Paciente obtenerPacienteOrThrow(Long idPaciente) {
        Paciente p = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("Paciente no encontrado."));
        if (!"A".equalsIgnoreCase(p.getEstado())) {
            throw new NegocioException("El paciente se encuentra en estado Inactivo / Dado de Alta. Debe presionar 'Re-activar Ficha / Re-ingreso' en la Ficha del Paciente antes de registrar nuevos datos.");
        }
        return p;
    }
}
