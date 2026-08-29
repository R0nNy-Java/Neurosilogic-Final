package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AlertaClinica;
import com.rrparedes.neurosilogic.model.EscalaGlasgow;
import com.rrparedes.neurosilogic.model.EvaluacionIMC;
import com.rrparedes.neurosilogic.model.SignoVital;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import org.springframework.stereotype.Service;

/**
 * Evalúa cada registro clínico contra sus rangos normales establecidos y genera
 * una alerta (por debajo o por encima de lo normal) cuando corresponde.
 */
@Service
public class AlertaClinicaService {

    // Rangos clínicos normales de referencia para un adulto en reposo.
    private static final double SISTOLICA_MIN = 90, SISTOLICA_MAX = 140;
    private static final double DIASTOLICA_MIN = 60, DIASTOLICA_MAX = 90;
    private static final double FC_MIN = 60, FC_MAX = 100;
    private static final double FR_MIN = 12, FR_MAX = 20;
    private static final double TEMP_MIN = 36.0, TEMP_MAX = 37.5;
    private static final double SAT_O2_MIN = 95;
    private static final double GLASGOW_NORMAL_MIN = 13;
    private static final double IMC_NORMAL_MIN = 18.5, IMC_NORMAL_MAX = 24.9;

    private final AlertaClinicaRepository alertaClinicaRepository;

    public AlertaClinicaService(AlertaClinicaRepository alertaClinicaRepository) {
        this.alertaClinicaRepository = alertaClinicaRepository;
    }

    /** Evalúa un registro de signos vitales; devuelve true si generó al menos una alerta. */
    public boolean evaluarSignosVitales(Long idPaciente, SignoVital sv) {
        boolean hayAlerta = false;
        hayAlerta |= chequear(idPaciente, "Signos Vitales", "Presión sistólica", sv.getPresionSistolica(), SISTOLICA_MIN, SISTOLICA_MAX, "mmHg");
        hayAlerta |= chequear(idPaciente, "Signos Vitales", "Presión diastólica", sv.getPresionDiastolica(), DIASTOLICA_MIN, DIASTOLICA_MAX, "mmHg");
        hayAlerta |= chequear(idPaciente, "Signos Vitales", "Frecuencia cardíaca", sv.getFrecuenciaCardiaca(), FC_MIN, FC_MAX, "ppm");
        hayAlerta |= chequear(idPaciente, "Signos Vitales", "Frecuencia respiratoria", sv.getFrecuenciaRespiratoria(), FR_MIN, FR_MAX, "rpm");
        if (sv.getTemperatura() != null) {
            hayAlerta |= chequear(idPaciente, "Signos Vitales", "Temperatura", sv.getTemperatura().doubleValue(), TEMP_MIN, TEMP_MAX, "°C");
        }
        if (sv.getSaturacionO2() != null && sv.getSaturacionO2() < SAT_O2_MIN) {
            guardar(idPaciente, "Signos Vitales", "BAJO",
                    "Saturación de oxígeno baja: " + sv.getSaturacionO2() + " % (mínimo normal " + (int) SAT_O2_MIN + " %)", "danger");
            hayAlerta = true;
        }
        return hayAlerta;
    }

    /** Evalúa una Escala de Glasgow; devuelve true si el puntaje está por debajo de lo normal. */
    public boolean evaluarGlasgow(Long idPaciente, EscalaGlasgow eg) {
        Integer puntaje = eg.getPuntajeTotal();
        if (puntaje != null && puntaje < GLASGOW_NORMAL_MIN) {
            String nivel = puntaje < 9 ? "Grave" : "Moderado";
            guardar(idPaciente, "Escala Glasgow", "BAJO",
                    "Puntaje de Glasgow " + puntaje + "/15 — nivel " + nivel + " (normal ≥ " + (int) GLASGOW_NORMAL_MIN + ")", "danger");
            return true;
        }
        return false;
    }

    /** Evalúa una Evaluación de IMC; devuelve true si el valor está fuera del rango de peso normal. */
    public boolean evaluarIMC(Long idPaciente, EvaluacionIMC imc) {
        Double valor = imc.getValorIMC();
        if (valor == null) return false;
        if (valor < IMC_NORMAL_MIN) {
            guardar(idPaciente, "Evaluación IMC", "BAJO",
                    "IMC bajo lo normal: " + String.format("%.2f", valor) + " (" + imc.getClasificacion() + ", normal " + IMC_NORMAL_MIN + "-" + IMC_NORMAL_MAX + ")", "warning");
            return true;
        } else if (valor > IMC_NORMAL_MAX) {
            guardar(idPaciente, "Evaluación IMC", "ALTO",
                    "IMC sobre lo normal: " + String.format("%.2f", valor) + " (" + imc.getClasificacion() + ", normal " + IMC_NORMAL_MIN + "-" + IMC_NORMAL_MAX + ")", "danger");
            return true;
        }
        return false;
    }

    private boolean chequear(Long idPaciente, String modulo, String parametro, Number valor, double min, double max, String unidad) {
        if (valor == null) return false;
        double v = valor.doubleValue();
        if (v < min) {
            guardar(idPaciente, modulo, "BAJO", parametro + " bajo lo normal: " + valor + " " + unidad + " (rango normal " + (int) min + "-" + (int) max + ")", "warning");
            return true;
        } else if (v > max) {
            guardar(idPaciente, modulo, "ALTO", parametro + " sobre lo normal: " + valor + " " + unidad + " (rango normal " + (int) min + "-" + (int) max + ")", "danger");
            return true;
        }
        return false;
    }

    private void guardar(Long idPaciente, String modulo, String nivelAlerta, String mensaje, String colorCodigo) {
        alertaClinicaRepository.save(new AlertaClinica(idPaciente, modulo, nivelAlerta, mensaje, colorCodigo));
    }
}
