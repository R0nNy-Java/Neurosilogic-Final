package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.*;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AntecedenteRepository;
import com.rrparedes.neurosilogic.repository.RangoSignoNormalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
            // La saturación de O2 solo tiene lado "bajo" (no existe un "sobre lo esperado" clínicamente
            // relevante aquí); mientras más lejos del mínimo, más grave — de azul (leve) a rojo (crítico).
            int diferencia = rango.getSatMin() - sv.getSaturacionO2();
            String color = diferencia >= 5 ? "danger" : "info";
            String nivel = diferencia >= 5 ? "CRÍTICO" : "BAJO";
            guardar(paciente, "Signos Vitales", nivel,
                    "Saturación de O2 bajo rango fisiológico adaptativo (" + rango.getNombreParametro() + "): " + sv.getSaturacionO2() + "% (esperado ≥ " + rango.getSatMin() + "%)", color);
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
            // El Glasgow no tiene "lado alto" (15 es lo mejor posible), así que su severidad va
            // de naranja (moderado) a rojo (grave) en vez de usar azul, que aquí no aplica.
            boolean grave = puntaje < 9;
            String nivel = grave ? "Grave" : "Moderado";
            guardar(paciente, "Escala Glasgow", "ALERTA",
                    "Puntaje de Glasgow " + puntaje + "/15 — Nivel " + nivel + " (Normal ≥ 13)", grave ? "danger" : "orange");
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
                    "IMC bajo el rango saludable: " + String.format("%.2f", valor) + " kg/m² (" + imc.getClasificacion() + ")", "info");
            hayAlerta = true;
        } else if (valor > 24.9) {
            String severidad = valor >= 30.0 ? "danger" : "orange";
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

    // ── Cálculo de severidad para pintar historiales (NO genera ni persiste alertas) ──
    // Usa la misma escala de 4 colores y los mismos umbrales que las alertas activas, para que
    // el historial de un módulo clínico se vea consistente con el panel de alertas del paciente:
    //   azul (info) = bajo, verde (success) = normal, naranja (orange) = alto moderado,
    //   rojo (danger) = alto crítico / severo.

    private static final List<String> PRIORIDAD_SEVERIDAD = List.of("danger", "orange", "info", "success");

    /** Clasifica un valor puntual contra su rango [min,max] esperado. */
    public String severidadPorRango(double valor, double min, double max) {
        if (valor < min) return "info";
        if (valor > max) {
            double margenSevero = max + (max - min) * 0.15;
            return valor > margenSevero ? "danger" : "orange";
        }
        return "success";
    }

    public String severidadGlasgow(Integer puntaje) {
        if (puntaje == null) return "success";
        if (puntaje >= 13) return "success";
        return puntaje < 9 ? "danger" : "orange";
    }

    public String severidadIMC(Double valorImc) {
        if (valorImc == null) return "success";
        if (valorImc < 18.5) return "info";
        if (valorImc > 24.9) return valorImc >= 30.0 ? "danger" : "orange";
        return "success";
    }

    /** El color más severo entre varios (para resumir un registro con varios parámetros a la vez). */
    private String peorSeveridad(List<String> colores) {
        for (String nivel : PRIORIDAD_SEVERIDAD) {
            if (colores.contains(nivel)) return nivel;
        }
        return "success";
    }

    /** Severidad resumida de un signo vital histórico, evaluando sus 5 parámetros contra el
     * rango adaptativo del paciente y devolviendo el peor de los colores encontrados. */
    public String severidadSignoVital(SignoVital sv, Paciente paciente) {
        RangoSignoNormal rango = obtenerRangoParaPaciente(paciente);
        List<String> colores = new ArrayList<>();
        if (sv.getPresionSistolica() != null) {
            colores.add(severidadPorRango(sv.getPresionSistolica(), rango.getSisMin(), rango.getSisMax()));
        }
        if (sv.getPresionDiastolica() != null) {
            colores.add(severidadPorRango(sv.getPresionDiastolica(), rango.getDiaMin(), rango.getDiaMax()));
        }
        if (sv.getFrecuenciaCardiaca() != null) {
            colores.add(severidadPorRango(sv.getFrecuenciaCardiaca(), rango.getFcMin(), rango.getFcMax()));
        }
        if (sv.getFrecuenciaRespiratoria() != null) {
            colores.add(severidadPorRango(sv.getFrecuenciaRespiratoria(), rango.getFrMin(), rango.getFrMax()));
        }
        if (sv.getTemperatura() != null) {
            colores.add(severidadPorRango(sv.getTemperatura().doubleValue(), rango.getTempMin().doubleValue(), rango.getTempMax().doubleValue()));
        }
        if (sv.getSaturacionO2() != null && sv.getSaturacionO2() < rango.getSatMin()) {
            int diferencia = rango.getSatMin() - sv.getSaturacionO2();
            colores.add(diferencia >= 5 ? "danger" : "info");
        }
        return peorSeveridad(colores);
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

    // Escala de severidad de 4 colores (uso general, no solo signos vitales):
    //   azul   = por debajo del rango normal (BAJO)
    //   verde  = dentro del rango normal (no genera alerta; se usa en la UI para el estado "sin alerta")
    //   naranja = por encima del rango, desviación moderada (ALTO)
    //   rojo   = por encima del rango, desviación severa (CRÍTICO)
    // El umbral que separa "moderado" de "severo" es 15% del ancho del rango normal por encima del máximo.
    private boolean chequear(Paciente paciente, String modulo, String parametro, Number valor, double min, double max, String unidad) {
        if (valor == null) return false;
        double v = valor.doubleValue();
        if (v < min) {
            guardar(paciente, modulo, "BAJO", parametro + " bajo lo esperado: " + valor + " " + unidad + " (Rango: " + (int) min + "-" + (int) max + ")", "info");
            return true;
        } else if (v > max) {
            double margenSevero = max + (max - min) * 0.15;
            boolean severo = v > margenSevero;
            guardar(paciente, modulo, severo ? "CRÍTICO" : "ALTO",
                    parametro + " sobre lo esperado: " + valor + " " + unidad + " (Rango: " + (int) min + "-" + (int) max + ")",
                    severo ? "danger" : "orange");
            return true;
        }
        return false;
    }

    private void guardar(Paciente paciente, String modulo, String nivelAlerta, String mensaje, String colorCodigo) {
        alertaClinicaRepository.save(new AlertaClinica(paciente, modulo, nivelAlerta, mensaje, colorCodigo));
    }
}
