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

    /** Registra y guarda el cierre de la ficha del paciente con un resumen del último enfermero y registros. */
    com.rrparedes.neurosilogic.model.CierreFicha registrarCierreFicha(Long idPaciente, com.rrparedes.neurosilogic.model.Usuario enfermero);

    /** Lista todos los cierres de ficha registrados ordenados por fecha descendente para el tablero Kanban. */
    List<com.rrparedes.neurosilogic.model.CierreFicha> obtenerCierresFicha();

    /** Lista cierres de ficha atribuibles a un enfermero en particular. */
    List<com.rrparedes.neurosilogic.model.CierreFicha> obtenerCierresFichaPorEnfermero(Long idUsuario);

    /** Lista pacientes cuya ficha activa pertenece al enfermero o no tienen cierre aún. */
    List<Paciente> obtenerPacientesEnEvaluacionPorEnfermero(Long idUsuario);

    /** Valida si el paciente cuenta con los datos mínimos obligatorios (Signos Vitales, Glasgow e IMC) para cerrar ficha. */
    boolean puedeCerrarFicha(Long idPaciente);

    /** Re-activa la ficha de un paciente dado de alta (cambia estado a 'A') y registra la auditoría de re-ingreso. */
    Paciente activarFicha(Long idPaciente, com.rrparedes.neurosilogic.model.Usuario enfermero);

    /** Otorga el alta a un paciente si no mantiene alertas activas, asigna estado 'I' y registra auditoría. */
    Paciente darDeAlta(Long idPaciente, com.rrparedes.neurosilogic.model.Usuario enfermero);
}
