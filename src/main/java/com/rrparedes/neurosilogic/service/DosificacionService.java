package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Dosificacion;
import com.rrparedes.neurosilogic.model.Paciente;

import java.util.List;
import java.util.Optional;

public interface DosificacionService {

    /** Busca un paciente por id (prioridad) o por cédula, para el formulario de dosificación. */
    Optional<Paciente> buscarPaciente(Long idPaciente, String cedula);

    List<Dosificacion> historial(Long idPaciente);

    Dosificacion registrar(Long idPaciente, String medicamento, Double dosisIndicada, String unidadDosis,
                           Double presentacion, String unidadPresentacion, Double diluyenteMl, Double horasTotales);
}
