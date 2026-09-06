package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> listarTodos();

    Medicamento registrar(Usuario actor, String nombre, String composicion, String dosisRecomendada);
}
