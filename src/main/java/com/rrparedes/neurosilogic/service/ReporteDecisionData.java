package com.rrparedes.neurosilogic.service;

import java.util.List;

/**
 * Métricas de apoyo a decisiones para el cierre de turno/semana/mes: qué tan cargado estuvo
 * el período, qué módulos generaron más alertas, cómo evolucionaron día a día, y sobre todo
 * qué pacientes necesitan atención prioritaria antes de terminar el turno.
 */
public record ReporteDecisionData(
        long totalAlertas,
        long alertasCriticas,
        long alertasAdvertencia,
        long registrosClinicosPeriodo,
        long cierresFichaPeriodo,
        List<ConteoEtiqueta> alertasPorModulo,
        List<ConteoEtiqueta> tendenciaDiaria,
        List<PacientePrioritario> pacientesPrioritarios
) {}
