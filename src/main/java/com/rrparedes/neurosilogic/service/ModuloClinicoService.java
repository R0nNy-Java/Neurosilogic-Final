package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Antecedente;
import com.rrparedes.neurosilogic.model.EscalaGlasgow;
import com.rrparedes.neurosilogic.model.Enfermedad;
import com.rrparedes.neurosilogic.model.EvaluacionIMC;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.SignoVital;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ModuloClinicoService {

    /** Busca un paciente por id (prioridad) o por cédula, para los formularios de captura clínica. */
    Optional<Paciente> buscarPaciente(Long idPaciente, String cedula);

    List<SignoVital> historialSignosVitales(Long idPaciente);

    /**
     * Color de severidad (azul/verde/naranja/rojo) por cada registro del historial, para pintar
     * la tabla igual que el panel de alertas del paciente. Clave = id del registro.
     */
    Map<Long, String> coloresHistorialSignosVitales(Long idPaciente);

    Map<Long, String> coloresHistorialGlasgow(Long idPaciente);

    Map<Long, String> coloresHistorialIMC(Long idPaciente);

    /** No guarda nada si algún valor está fuera de rango físico posible (validación estricta de backend). */
    void registrarSignoVital(Long idPaciente, Integer presionSistolica, Integer presionDiastolica,
                             Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
                             Double temperatura, Integer saturacionOxigeno);

    List<EscalaGlasgow> historialGlasgow(Long idPaciente);

    EscalaGlasgow registrarGlasgow(Long idPaciente, Integer respuestaOcular, Integer respuestaVerbal, Integer respuestaMotora);

    List<EvaluacionIMC> historialIMC(Long idPaciente);

    /** No guarda nada si peso/estatura son nulos o no positivos (validación estricta de backend). */
    void registrarIMC(Long idPaciente, Double pesoKg, Double estaturaM);

    List<Antecedente> historialAntecedentes(Long idPaciente);

    List<Enfermedad> listarEnfermedades();

    Antecedente registrarAntecedente(Long idPaciente, String tipo, String enfermedadSeleccionada, String descripcion);
}
