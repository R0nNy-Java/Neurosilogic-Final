package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {

    List<Paciente> listarTodos();

    long contarTodos();

    Optional<Paciente> buscarPorId(Long id);

    Optional<Paciente> buscarPorCedula(String cedula);

    /**
     * Registra o actualiza un paciente. Lanza {@link NegocioException} si se intenta
     * crear uno nuevo con una cédula ya registrada.
     */
    Paciente registrar(Paciente paciente);

    /** Agrega la ficha completa del paciente (signos vitales, Glasgow, IMC, antecedentes, alertas). */
    Optional<PanelPacienteData> obtenerPanel(Long id);
}
