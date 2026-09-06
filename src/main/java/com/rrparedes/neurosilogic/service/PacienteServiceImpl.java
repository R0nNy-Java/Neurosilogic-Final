package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
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

    public PacienteServiceImpl(PacienteRepository pacienteRepository,
                               SignoVitalRepository signoVitalRepository,
                               EscalaGlasgowRepository escalaGlasgowRepository,
                               EvaluacionIMCRepository evaluacionIMCRepository,
                               AntecedenteRepository antecedenteRepository,
                               AlertaClinicaRepository alertaClinicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.alertaClinicaRepository = alertaClinicaRepository;
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
}
