package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AlertaClinica;
import com.rrparedes.neurosilogic.model.AuditoriaAcceso;
import com.rrparedes.neurosilogic.model.CierreFicha;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;

import java.util.List;

/**
 * Datos de la pantalla de Reportería. Los campos de la vista de administrador
 * (todosLosCierres, todasLasAlertas, todosLosMovimientos, listaEnfermeros) quedan en null
 * cuando el usuario no es administrador, y viceversa con misCierresFicha.
 */
public record ReporteResumenPeriodo(
        List<CierreFicha> todosLosCierres,
        List<AlertaClinica> todasLasAlertas,
        List<AuditoriaAcceso> todosLosMovimientos,
        List<Usuario> listaEnfermeros,
        List<Paciente> todosLosPacientes,
        List<CierreFicha> misCierresFicha,
        ReporteDecisionData decision
) {}
