package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;

import java.util.List;

public interface MedicamentoService {

    List<Medicamento> listarTodos();

    Medicamento registrar(Usuario actor, String nombre, String composicion, String dosisRecomendada, Integer stock);

    /** Elimina un medicamento del catálogo. No afecta dosificaciones ya registradas (el historial
     * guarda el nombre como texto libre, no una referencia al catálogo). */
    void eliminar(Usuario actor, Long idMedicamento);

    /** Suma {@code cantidad} unidades al stock actual del medicamento (reabastecimiento). Lanza
     * {@link NegocioException} si la cantidad no es positiva o si el medicamento no existe. */
    Medicamento agregarStock(Usuario actor, Long idMedicamento, Integer cantidad);

    /** Edita los datos de un medicamento del catálogo. Lanza {@link NegocioException} si el
     * medicamento no existe. */
    Medicamento editar(Usuario actor, Long idMedicamento, String nombre, String composicion, String dosisRecomendada, Integer stock);
}
