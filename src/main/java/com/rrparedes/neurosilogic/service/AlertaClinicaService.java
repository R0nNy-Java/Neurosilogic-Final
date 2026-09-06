package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
import com.rrparedes.neurosilogic.repository.RangoSignoNormalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Evalúa cada registro clínico contra sus rangos normales adaptativos según las enfermedades
 * antecedentes del paciente y genera o resuelve alertas dinámicas cuando corresponde.
 */
@Service
public class AlertaClinicaService {

    private final AlertaClinicaRepository alertaClinicaRepository;
    private final AntecedenteRepository antecedenteRepository;
    private final RangoSignoNormalRepository rangoSignoNormalRepository;

    public AlertaClinicaService(AlertaClinicaRepository alertaClinicaRepository,
                                AntecedenteRepository antecedenteRepository,
                                RangoSignoNormalRepository rangoSignoNormalRepository) {
        this.alertaClinicaRepository = alertaClinicaRepository;
        this.antecedenteRepository = antecedenteRepository;
        this.rangoSignoNormalRepository = rangoSignoNormalRepository;
    }

    /**
     * Evalúa un nuevo registro de signos vitales. Si los nuevos valores están dentro
     * de los rangos esperados para el paciente, elimina/resuelve las alertas anteriores de este módulo.
     */
    @Transactional
    public boolean evaluarSignosVitales(Paciente paciente, SignoVital sv) {
        // Al ingresar una nueva medición, limpiamos las alertas anteriores de este módulo para resolverlas si mejoró
        alertaClinicaRepository.deleteByPacienteIdPacienteAndModulo(paciente.getIdPaciente(), "Signos Vitales");

        RangoSignoNormal rango = obtenerRangoParaPaciente(paciente);
        boolean hayAlerta = false;

        hayAlerta |= chequear(paciente, "Signos Vitales", "Presión sistólica", sv.getPresionSistolica(), rango.getSisMin(), rango.getSisMax(), "mmHg");
        hayAlerta |= chequear(paciente, "Signos Vitales", "Presión diastólica", sv.getPresionDiastolica(), rango.getDiaMin(), rango.getDiaMax(), "mmHg");
        hayAlerta |= chequear(paciente, "Signos Vitales", "Frecuencia cardíaca", sv.getFrecuenciaCardiaca(), rango.getFcMin(), rango.getFcMax(), "ppm");
        hayAlerta |= chequear(paciente, "Signos Vitales", "Frecuencia respiratoria", sv.getFrecuenciaRespiratoria(), rango.getFrMin(), rango.getFrMax(), "rpm");

        if (sv.getTemperatura() != null) {
            hayAlerta |= chequear(paciente, "Signos Vitales", "Temperatura", sv.getTemperatura().doubleValue(), rango.getTempMin().doubleValue(), rango.getTempMax().doubleValue(), "°C");
        }

        if (sv.getSaturacionO2() != null && sv.getSaturacionO2() < rango.getSatMin()) {
            guardar(paciente, "Signos Vitales", "CRÍTICO",
                    "Saturación de O2 bajo rango fisiológico adaptativo (" + rango.getNombreParametro() + "): " + sv.getSaturacionO2() + "% (esperado ≥ " + rango.getSatMin() + "%)", "danger");
            hayAlerta = true;
        }

        return hayAlerta;
    }

    /** Evalúa una Escala de Glasgow. Si el puntaje vuelve a ser normal (≥ 13), elimina la alerta anterior. */
    @Transactional
    public boolean evaluarGlasgow(Paciente paciente, EscalaGlasgow eg) {
        alertaClinicaRepository.deleteByPacienteIdPacienteAndModulo(paciente.getIdPaciente(), "Escala Glasgow");

        Integer puntaje = eg.getPuntajeTotal();
        if (puntaje != null && puntaje < 13) {
            String nivel = puntaje < 9 ? "Grave" : "Moderado";
            guardar(paciente, "Escala Glasgow", "ALERTA",
                    "Puntaje de Glasgow " + puntaje + "/15 — Nivel " + nivel + " (Normal ≥ 13)", "danger");
            return true;
        }
        return false;
    }

    /** Evalúa el IMC del paciente. Si se normaliza, resuelve las alertas de IMC anteriores. */
    @Transactional
    public boolean evaluarIMC(Paciente paciente, EvaluacionIMC imc) {
        alertaClinicaRepository.deleteByPacienteIdPacienteAndModulo(paciente.getIdPaciente(), "Evaluación IMC");

        Double valor = imc.getValorIMC();
        if (valor == null) return false;

        boolean hayAlerta = false;
        boolean tieneEnfermedadCardiaca = tienePatologia(paciente, "Insuficiencia Cardíaca");

        if (valor < 18.5) {
            guardar(paciente, "Evaluación IMC", "DESNUTRICIÓN / BAJO PESO",
                    "IMC bajo el rango saludable: " + String.format("%.2f", valor) + " kg/m² (" + imc.getClasificacion() + ")", "warning");
            hayAlerta = true;
        } else if (valor > 24.9) {
            String severidad = valor >= 30.0 ? "danger" : "warning";
            String mensaje = "IMC sobre lo saludable: " + String.format("%.2f", valor) + " kg/m² (" + imc.getClasificacion() + ")";

            if (tieneEnfermedadCardiaca) {
                mensaje += " ⚠️ ALERTA CARDÍACA: Paciente con Insuficiencia Cardíaca. Evaluar retención de líquidos / edema.";
                severidad = "danger";
            }

            guardar(paciente, "Evaluación IMC", "SOBREPESO / OBESIDAD", mensaje, severidad);
            hayAlerta = true;
        }

        return hayAlerta;
    }

    // Antes esto comparaba el texto libre de "Observacion" contra el nombre de la enfermedad y
    // nunca coincidía (Observacion guardaba "Enfermedad - descripción del enfermero"). Ahora se
    // usa la relación real Antecedente → Enfermedad, que no depende de parsear texto.
    private RangoSignoNormal obtenerRangoParaPaciente(Paciente paciente) {
        List<Antecedente> antecedentes = antecedenteRepository.findByPacienteIdPaciente(paciente.getIdPaciente());
        for (Antecedente ant : antecedentes) {
            if (ant.getEnfermedad() != null) {
                Optional<RangoSignoNormal> rangoOpt = rangoSignoNormalRepository.findByIdEnfermedad(ant.getEnfermedad().getIdEnfermedad());
                if (rangoOpt.isPresent()) {
                    return rangoOpt.get(); // Retorna el rango adaptativo de la patología
                }
            }
        }
        // Retorna rango estándar sano si no tiene patología asociada con rango especial
        return rangoSignoNormalRepository.findByIdEnfermedadIsNull()
                .orElse(new RangoSignoNormal(null, "Persona Sana Estándar", 36.0, 37.4, 90, 120, 60, 80, 60, 100, 12, 20, 95, 100));
    }

    private boolean tienePatologia(Paciente paciente, String fragmentoNombre) {
        List<Antecedente> antecedentes = antecedenteRepository.findByPacienteIdPaciente(paciente.getIdPaciente());
        return antecedentes.stream().anyMatch(a -> a.getEnfermedad() != null
                && a.getEnfermedad().getNombreEnfermedad() != null
                && a.getEnfermedad().getNombreEnfermedad().toLowerCase().contains(fragmentoNombre.toLowerCase()));
    }

    private boolean chequear(Paciente paciente, String modulo, String parametro, Number valor, double min, double max, String unidad) {
        if (valor == null) return false;
        double v = valor.doubleValue();
        if (v < min) {
            guardar(paciente, modulo, "BAJO", parametro + " bajo lo esperado: " + valor + " " + unidad + " (Rango: " + (int) min + "-" + (int) max + ")", "warning");
            return true;
        } else if (v > max) {
            guardar(paciente, modulo, "ALTO", parametro + " sobre lo esperado: " + valor + " " + unidad + " (Rango: " + (int) min + "-" + (int) max + ")", "danger");
            return true;
        }
        return false;
    }

    private void guardar(Paciente paciente, String modulo, String nivelAlerta, String mensaje, String colorCodigo) {
        alertaClinicaRepository.save(new AlertaClinica(paciente, modulo, nivelAlerta, mensaje, colorCodigo));
    }
}
