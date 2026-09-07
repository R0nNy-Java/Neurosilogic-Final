package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Dosificacion;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.repository.DosificacionRepository;
import com.rrparedes.neurosilogic.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DosificacionServiceImpl implements DosificacionService {

    private final PacienteRepository pacienteRepository;
    private final DosificacionRepository dosificacionRepository;

    public DosificacionServiceImpl(PacienteRepository pacienteRepository, DosificacionRepository dosificacionRepository) {
        this.pacienteRepository = pacienteRepository;
        this.dosificacionRepository = dosificacionRepository;
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

    @Override
    @Transactional(readOnly = true)
    public List<Dosificacion> historial(Long idPaciente) {
        return dosificacionRepository.findByPacienteIdPacienteOrderByFechaRegistroDesc(idPaciente);
    }

    @Override
    public Dosificacion registrar(Long idPaciente, String medicamento, Double dosisIndicada, String unidadDosis,
                                  Double presentacion, String unidadPresentacion, Double diluyenteMl, Double horasTotales) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("Paciente no encontrado."));

        if (!"A".equalsIgnoreCase(paciente.getEstado())) {
            throw new NegocioException("El paciente se encuentra en estado Inactivo / Dado de Alta. Debe presionar 'Re-activar Ficha / Re-ingreso' en la Ficha del Paciente antes de registrar nuevas tomas de dosificación.");
        }

        Dosificacion d = new Dosificacion();
        d.setPaciente(paciente);
        d.setMedicamento(medicamento);
        d.setUnidadDosis(unidadDosis);
        d.setUnidadPresentacion(unidadPresentacion);
        d.setPresentacion(presentacion);
        d.setDiluyenteMl(diluyenteMl);
        d.setHorasTotales(horasTotales);
        d.setDosisIndicada(dosisIndicada);
        return dosificacionRepository.save(d);
    }
}
