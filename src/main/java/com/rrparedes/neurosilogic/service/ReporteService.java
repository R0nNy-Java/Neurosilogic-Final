package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AuditoriaAcceso;
import com.rrparedes.neurosilogic.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

public interface ReporteService {

    /** Últimos movimientos de auditoría (login, bloqueos, etc.) para el dashboard. */
    List<AuditoriaAcceso> obtenerUltimosMovimientos();

    /**
     * Arma los datos de la pantalla de Reportería según el rol de {@code usuarioLogueado}
     * y, opcionalmente, un rango de fechas (ambos null = sin filtro de fecha).
     */
    ReporteResumenPeriodo generarReportePeriodo(Usuario usuarioLogueado, LocalDateTime inicio, LocalDateTime fin);
}
