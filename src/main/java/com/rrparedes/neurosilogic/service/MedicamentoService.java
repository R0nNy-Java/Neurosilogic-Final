package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> listarTodos();

    Medicamento registrar(Usuario actor, String nombre, String composicion, String dosisRecomendada);

    /** Elimina un medicamento del catálogo. No afecta dosificaciones ya registradas (el historial
     * guarda el nombre como texto libre, no una referencia al catálogo). */
    void eliminar(Usuario actor, Long idMedicamento);
}
