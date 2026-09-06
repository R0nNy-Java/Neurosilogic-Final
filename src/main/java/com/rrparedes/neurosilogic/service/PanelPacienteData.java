package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AlertaClinica;
import com.rrparedes.neurosilogic.model.Antecedente;
import com.rrparedes.neurosilogic.model.EscalaGlasgow;
import com.rrparedes.neurosilogic.model.EvaluacionIMC;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.SignoVital;

import java.util.List;

/** Agregado de datos que necesita el panel/ficha clínica de un paciente. */
public record PanelPacienteData(
        Paciente paciente,
        List<SignoVital> signosVitales,
        List<EscalaGlasgow> glasgowList,
        List<EvaluacionIMC> imcList,
        List<Antecedente> antecedentesList,
        List<AlertaClinica> alertas
) {}
