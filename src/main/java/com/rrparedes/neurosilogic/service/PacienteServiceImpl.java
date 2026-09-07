package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
import com.rrparedes.neurosilogic.repository.AuditoriaAccesoRepository;
import com.rrparedes.neurosilogic.repository.CierreFichaRepository;
import com.rrparedes.neurosilogic.repository.DosificacionRepository;
import com.rrparedes.neurosilogic.repository.EscalaGlasgowRepository;
import com.rrparedes.neurosilogic.repository.EvaluacionIMCRepository;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import com.rrparedes.neurosilogic.repository.SignoVitalRepository;
import com.rrparedes.neurosilogic.util.CedulaEcuatorianaValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final AuditoriaAccesoRepository auditoriaAccesoRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository,
                               SignoVitalRepository signoVitalRepository,
                               EscalaGlasgowRepository escalaGlasgowRepository,
                               EvaluacionIMCRepository evaluacionIMCRepository,
                               AntecedenteRepository antecedenteRepository,
                               AlertaClinicaRepository alertaClinicaRepository,
                               CierreFichaRepository cierreFichaRepository,
                               DosificacionRepository dosificacionRepository,
                               AuditoriaAccesoRepository auditoriaAccesoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.alertaClinicaRepository = alertaClinicaRepository;
        this.cierreFichaRepository = cierreFichaRepository;
        this.dosificacionRepository = dosificacionRepository;
        this.auditoriaAccesoRepository = auditoriaAccesoRepository;
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
        if (paciente.getEstado() == null || paciente.getEstado().trim().isEmpty()) {
            paciente.setEstado("A");
        }
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
    @Transactional(readOnly = true)
    public boolean puedeCerrarFicha(Long idPaciente) {
        if (idPaciente == null) return false;
        boolean tieneSV = !signoVitalRepository.findByPacienteIdPaciente(idPaciente).isEmpty();
        boolean tieneGlasgow = !escalaGlasgowRepository.findByPacienteIdPaciente(idPaciente).isEmpty();
        boolean tieneIMC = !evaluacionIMCRepository.findByPacienteIdPaciente(idPaciente).isEmpty();
        return tieneSV && tieneGlasgow && tieneIMC;
    }

    @Override
    public Paciente activarFicha(Long idPaciente, Usuario enfermero) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("No se encontró el paciente especificado."));

        paciente.setEstado("A");
        Paciente guardado = pacienteRepository.save(paciente);

        String motivo = "Ficha clínica de " + paciente.getNombres() + " " + paciente.getApellidos() +
                " (C.I. " + paciente.getCedula() + ") reactivada para atención clínica por re-ingreso.";

        auditoriaAccesoRepository.save(new com.rrparedes.neurosilogic.model.AuditoriaAcceso(
                enfermero, paciente, "REINGRESO_PACIENTE", motivo, LocalDateTime.now()
        ));

        return guardado;
    }

    @Override
    public Paciente darDeAlta(Long idPaciente, Usuario enfermero) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("No se encontró el paciente especificado."));

        List<com.rrparedes.neurosilogic.model.AlertaClinica> alertas = alertaClinicaRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente);
        if (!alertas.isEmpty()) {
            throw new NegocioException("No se puede dar de alta a un paciente mientras mantenga alertas clínicas activas. Se requiere estabilización médica antes del egreso.");
        }

        paciente.setEstado("I");
        Paciente guardado = pacienteRepository.save(paciente);

        String motivo = "Alta Otorgada por estabilidad médica (cero alertas activas) por " +
                ((enfermero != null) ? (enfermero.getNombres() + " " + enfermero.getApellidos()) : "Personal Clínico") + ".";

        auditoriaAccesoRepository.save(new com.rrparedes.neurosilogic.model.AuditoriaAcceso(
                enfermero, paciente, "ALTA_OTORGADA_ESTABLE", motivo, LocalDateTime.now()
        ));

        return guardado;
    }

    @Override
    public com.rrparedes.neurosilogic.model.CierreFicha registrarCierreFicha(Long idPaciente, Usuario enfermero) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("No se encontró el paciente especificado."));

        if (!puedeCerrarFicha(idPaciente)) {
            throw new NegocioException("No se puede realizar el Cierre de Ficha. Debe registrar al menos los Signos Vitales, Escala de Glasgow y Evaluación IMC antes de finalizar la atención del paciente.");
        }

        Optional<com.rrparedes.neurosilogic.model.CierreFicha> ultimoCierreOpt = cierreFichaRepository.findTopByPaciente_IdPacienteOrderByFechaCierreDesc(idPaciente);

        List<com.rrparedes.neurosilogic.model.SignoVital> svList = signoVitalRepository.findByPacienteIdPaciente(idPaciente);
        List<com.rrparedes.neurosilogic.model.EscalaGlasgow> egList = escalaGlasgowRepository.findByPacienteIdPaciente(idPaciente);
        List<com.rrparedes.neurosilogic.model.EvaluacionIMC> imcList = evaluacionIMCRepository.findByPacienteIdPaciente(idPaciente);
        List<com.rrparedes.neurosilogic.model.Antecedente> antList = antecedenteRepository.findByPacienteIdPaciente(idPaciente);
        List<com.rrparedes.neurosilogic.model.Dosificacion> dosList = dosificacionRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente);

        boolean huboNuevasTomas = true;
        if (ultimoCierreOpt.isPresent()) {
            LocalDateTime fechaUltimoCierre = ultimoCierreOpt.get().getFechaCierre();
            boolean nuevaSV = !svList.isEmpty() && svList.get(0).getFechaHora() != null && svList.get(0).getFechaHora().isAfter(fechaUltimoCierre);
            boolean nuevaEG = !egList.isEmpty() && egList.get(0).getFechaHora() != null && egList.get(0).getFechaHora().isAfter(fechaUltimoCierre);
            boolean nuevaIMC = !imcList.isEmpty() && imcList.get(0).getFechaRegistro() != null && imcList.get(0).getFechaRegistro().isAfter(fechaUltimoCierre);
            boolean nuevaDos = !dosList.isEmpty() && dosList.get(0).getFechaRegistro() != null && dosList.get(0).getFechaRegistro().isAfter(fechaUltimoCierre);
            huboNuevasTomas = nuevaSV || nuevaEG || nuevaIMC || nuevaDos;
        }

        // Si NO hubo nuevas tomas respecto al último cierre, se mantiene el cierre previo sin duplicados en BD
        if (ultimoCierreOpt.isPresent() && !huboNuevasTomas) {
            auditoriaAccesoRepository.save(new com.rrparedes.neurosilogic.model.AuditoriaAcceso(
                    enfermero, paciente, "CIERRE_FICHA_CONSERVADO", "Cierre de Ficha registrado para " + paciente.getNombres() + " " + paciente.getApellidos() + " (se conserva último cierre sin duplicados).", LocalDateTime.now()
            ));
            return ultimoCierreOpt.get();
        }

        // Si hubo nuevas tomas o es el primer cierre
        String nombreEnfermero = (enfermero != null) ? (enfermero.getNombres() + " " + enfermero.getApellidos()) : "Enfermero de Turno";

        String ultimosSV = svList.isEmpty() ? "Sin registro de signos vitales." :
                ("PA: " + svList.get(0).getPresionArterial() + ", FC: " + svList.get(0).getFrecuenciaCardiaca() + "ppm, Temp: " + svList.get(0).getTemperatura() + "°C, Sat: " + svList.get(0).getSaturacionOxigeno() + "%");

        String ultimoGlasgow = egList.isEmpty() ? "Sin evaluación de Glasgow." :
                ("Puntaje: " + egList.get(0).getPuntajeTotal() + "/15 (" + egList.get(0).getClasificacion() + ")");

        String ultimoIMC = imcList.isEmpty() ? "Sin evaluación de IMC." :
                ("IMC: " + String.format("%.2f", imcList.get(0).getValorIMC()) + " kg/m² (" + imcList.get(0).getClasificacion() + ")");

        String ultimosAntecedentes = antList.isEmpty() ? "Sin antecedentes registrados." :
                (antList.get(0).getTipo() + ": " + antList.get(0).getObservacion());

        String ultimaDosis = dosList.isEmpty() ? "Sin administración de dosis registrada." :
                (dosList.get(0).getMedicamento() + " - " + dosList.get(0).getDosisIndicada() + " " + dosList.get(0).getUnidadDosis() + " (" + String.format("%.2f", dosList.get(0).getVolumenAdministrarMl()) + " ml)");

        com.rrparedes.neurosilogic.model.CierreFicha cierre = new com.rrparedes.neurosilogic.model.CierreFicha();
        cierre.setPaciente(paciente);
        cierre.setEnfermero(enfermero);
        cierre.setNombreEnfermero(nombreEnfermero);
        cierre.setUltimosSignosVitales(ultimosSV);
        cierre.setUltimoGlasgow(ultimoGlasgow);
        cierre.setUltimoIMC(ultimoIMC);
        cierre.setUltimosAntecedentes(ultimosAntecedentes);
        cierre.setUltimaDosis(ultimaDosis);
        cierre.setFechaCierre(LocalDateTime.now());

        boolean tieneAlertas = !alertaClinicaRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente).isEmpty();
        cierre.setTieneAlertaActiva(tieneAlertas);

        com.rrparedes.neurosilogic.model.CierreFicha guardado = cierreFichaRepository.save(cierre);

        auditoriaAccesoRepository.save(new com.rrparedes.neurosilogic.model.AuditoriaAcceso(
                enfermero, paciente, "CIERRE_FICHA_REGISTRADO", "Cierre de Ficha guardado exitosamente en la base de datos para " + paciente.getNombres() + " " + paciente.getApellidos() + ".", LocalDateTime.now()
        ));

        return guardado;
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
        List<Paciente> todos = pacienteRepository.findAll();
        List<Paciente> resultado = new ArrayList<>();
        for (Paciente p : todos) {
            // Solo considerar pacientes ACTIVOS ('A')
            if (!"A".equalsIgnoreCase(p.getEstado())) {
                continue;
            }
            Optional<com.rrparedes.neurosilogic.model.CierreFicha> ultimoCierre = cierreFichaRepository.findTopByPaciente_IdPacienteOrderByFechaCierreDesc(p.getIdPaciente());
            if (idUsuario == null || ultimoCierre.isEmpty() || (ultimoCierre.get().getEnfermero() != null && ultimoCierre.get().getEnfermero().getIdUsuario().equals(idUsuario))) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
