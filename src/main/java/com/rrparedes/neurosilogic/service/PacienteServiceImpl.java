package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
import com.rrparedes.neurosilogic.repository.CierreFichaRepository;
import com.rrparedes.neurosilogic.repository.DosificacionRepository;
import com.rrparedes.neurosilogic.repository.EscalaGlasgowRepository;
import com.rrparedes.neurosilogic.repository.EvaluacionIMCRepository;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import com.rrparedes.neurosilogic.repository.SignoVitalRepository;
import com.rrparedes.neurosilogic.util.CedulaEcuatorianaValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final EscalaGlasgowRepository escalaGlasgowRepository;
    private final EvaluacionIMCRepository evaluacionIMCRepository;
    private final AntecedenteRepository antecedenteRepository;
    private final AlertaClinicaRepository alertaClinicaRepository;
    private final CierreFichaRepository cierreFichaRepository;
    private final DosificacionRepository dosificacionRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository,
                               SignoVitalRepository signoVitalRepository,
                               EscalaGlasgowRepository escalaGlasgowRepository,
                               EvaluacionIMCRepository evaluacionIMCRepository,
                               AntecedenteRepository antecedenteRepository,
                               AlertaClinicaRepository alertaClinicaRepository,
                               CierreFichaRepository cierreFichaRepository,
                               DosificacionRepository dosificacionRepository) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.alertaClinicaRepository = alertaClinicaRepository;
        this.cierreFichaRepository = cierreFichaRepository;
        this.dosificacionRepository = dosificacionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTodos() {
        return pacienteRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Long id) {
        return pacienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorCedula(String cedula) {
        return pacienteRepository.findByCedula(cedula);
    }

    @Override
    public Paciente registrar(Paciente paciente) {
        if (!CedulaEcuatorianaValidator.esValida(paciente.getCedula())) {
            throw new NegocioException("La cédula ingresada no es válida.");
        }
        if (paciente.getIdPaciente() == null && pacienteRepository.existsByCedula(paciente.getCedula())) {
            throw new NegocioException("Ya existe un paciente registrado con la cédula ingresada.");
        }
        paciente.setEstado("A");
        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PanelPacienteData> obtenerPanel(Long id) {
        return pacienteRepository.findById(id).map(paciente -> new PanelPacienteData(
                paciente,
                signoVitalRepository.findByPacienteIdPaciente(id),
                escalaGlasgowRepository.findByPacienteIdPaciente(id),
                evaluacionIMCRepository.findByPacienteIdPaciente(id),
                antecedenteRepository.findByPacienteIdPaciente(id),
                alertaClinicaRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(id)
        ));
    }

    @Override
    public com.rrparedes.neurosilogic.model.CierreFicha registrarCierreFicha(Long idPaciente, com.rrparedes.neurosilogic.model.Usuario enfermero) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("No se encontró el paciente especificado."));

        String nombreEnfermero = (enfermero != null) ? (enfermero.getNombres() + " " + enfermero.getApellidos()) : "Enfermero de Turno";

        // Obtener últimos registros
        List<com.rrparedes.neurosilogic.model.SignoVital> svList = signoVitalRepository.findByPacienteIdPaciente(idPaciente);
        String ultimosSV = svList.isEmpty() ? "Sin registro de signos vitales." :
                ("PA: " + svList.get(0).getPresionArterial() + ", FC: " + svList.get(0).getFrecuenciaCardiaca() + "ppm, Temp: " + svList.get(0).getTemperatura() + "°C, Sat: " + svList.get(0).getSaturacionOxigeno() + "%");

        List<com.rrparedes.neurosilogic.model.EscalaGlasgow> egList = escalaGlasgowRepository.findByPacienteIdPaciente(idPaciente);
        String ultimoGlasgow = egList.isEmpty() ? "Sin evaluación de Glasgow." :
                ("Puntaje: " + egList.get(0).getPuntajeTotal() + "/15 (" + egList.get(0).getClasificacion() + ")");

        List<com.rrparedes.neurosilogic.model.EvaluacionIMC> imcList = evaluacionIMCRepository.findByPacienteIdPaciente(idPaciente);
        String ultimoIMC = imcList.isEmpty() ? "Sin evaluación de IMC." :
                ("IMC: " + String.format("%.2f", imcList.get(0).getValorIMC()) + " kg/m² (" + imcList.get(0).getClasificacion() + ")");

        List<com.rrparedes.neurosilogic.model.Antecedente> antList = antecedenteRepository.findByPacienteIdPaciente(idPaciente);
        String ultimosAntecedentes = antList.isEmpty() ? "Sin antecedentes registrados." :
                (antList.get(0).getTipo() + ": " + antList.get(0).getObservacion());

        List<com.rrparedes.neurosilogic.model.Dosificacion> dosList = dosificacionRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente);
        String ultimaDosis = dosList.isEmpty() ? "Sin administración de dosis registrada." :
                (dosList.get(0).getMedicamento() + " - " + dosList.get(0).getDosisIndicada() + " " + dosList.get(0).getUnidadDosis() + " (" + String.format("%.2f", dosList.get(0).getVolumenAdministrarMl()) + " ml)");

        com.rrparedes.neurosilogic.model.CierreFicha cierre = cierreFichaRepository.findTopByPaciente_IdPacienteOrderByFechaCierreDesc(idPaciente)
                .orElse(new com.rrparedes.neurosilogic.model.CierreFicha());

        cierre.setPaciente(paciente);
        cierre.setEnfermero(enfermero);
        cierre.setNombreEnfermero(nombreEnfermero);
        cierre.setUltimosSignosVitales(ultimosSV);
        cierre.setUltimoGlasgow(ultimoGlasgow);
        cierre.setUltimoIMC(ultimoIMC);
        cierre.setUltimosAntecedentes(ultimosAntecedentes);
        cierre.setUltimaDosis(ultimaDosis);
        cierre.setFechaCierre(java.time.LocalDateTime.now());

        boolean tieneAlertas = !alertaClinicaRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente).isEmpty();
        cierre.setTieneAlertaActiva(tieneAlertas);

        return cierreFichaRepository.save(cierre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.rrparedes.neurosilogic.model.CierreFicha> obtenerCierresFicha() {
        return cierreFichaRepository.findAllByOrderByFechaCierreDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.rrparedes.neurosilogic.model.CierreFicha> obtenerCierresFichaPorEnfermero(Long idUsuario) {
        if (idUsuario == null) return List.of();
        return cierreFichaRepository.findByEnfermeroIdUsuarioOrderByFechaCierreDesc(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> obtenerPacientesEnEvaluacionPorEnfermero(Long idUsuario) {
        if (idUsuario == null) return pacienteRepository.findAll();
        List<Paciente> todos = pacienteRepository.findAll();
        List<Paciente> resultado = new java.util.ArrayList<>();
        for (Paciente p : todos) {
            Optional<com.rrparedes.neurosilogic.model.CierreFicha> ultimoCierre = cierreFichaRepository.findTopByPaciente_IdPacienteOrderByFechaCierreDesc(p.getIdPaciente());
            // Si no tiene cierre o si el último cierre fue realizado por ESTE enfermero
            if (ultimoCierre.isEmpty() || (ultimoCierre.get().getEnfermero() != null && ultimoCierre.get().getEnfermero().getIdUsuario().equals(idUsuario))) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
