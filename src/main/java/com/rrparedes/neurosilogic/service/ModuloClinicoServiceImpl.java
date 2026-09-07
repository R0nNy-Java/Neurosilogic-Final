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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
    public Map<Long, String> coloresHistorialSignosVitales(Long idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente).orElse(null);
        if (paciente == null) return Map.of();
        return historialSignosVitales(idPaciente).stream()
                .collect(Collectors.toMap(SignoVital::getIdSignoVital, sv -> alertaClinicaService.severidadSignoVital(sv, paciente)));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> coloresHistorialGlasgow(Long idPaciente) {
        return historialGlasgow(idPaciente).stream()
                .collect(Collectors.toMap(EscalaGlasgow::getIdGlasgow, eg -> alertaClinicaService.severidadGlasgow(eg.getPuntajeTotal())));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> coloresHistorialIMC(Long idPaciente) {
        return historialIMC(idPaciente).stream()
                .collect(Collectors.toMap(EvaluacionIMC::getIdIMC, imc -> alertaClinicaService.severidadIMC(imc.getValorIMC())));
    }

    @Override
    public void registrarSignoVital(Long idPaciente, Integer presionSistolica, Integer presionDiastolica,
                                    Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
                                    Double temperatura, Integer saturacionOxigeno) {
        // Validación estricta backend: impedir valores negativos o fuera de rango físico.
        // Antes esto hacía "return" en silencio: el enfermero veía la página recargarse
        // "normal" creyendo que el signo vital quedó guardado, cuando en realidad no se
        // registró nada. Ahora se avisa explícitamente con el mismo mecanismo de mensaje de
        // error que usa el resto de la aplicación.
        if (presionSistolica <= 0 || presionDiastolica <= 0 || frecuenciaCardiaca <= 0 ||
                frecuenciaRespiratoria <= 0 || temperatura <= 0 || saturacionOxigeno <= 0 || saturacionOxigeno > 100) {
            throw new NegocioException("Los signos vitales ingresados están fuera de un rango físicamente válido. No se guardó el registro; verifique los valores e intente de nuevo.");
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
        // Validación estricta backend: impedir números negativos o improbables. Antes hacía
        // "return" en silencio (ver misma nota en registrarSignoVital) — ahora avisa.
        if (pesoKg == null || pesoKg <= 0 || estaturaM == null || estaturaM <= 0) {
            throw new NegocioException("El peso y la estatura deben ser valores mayores a 0. No se guardó el registro; verifique los valores e intente de nuevo.");
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
