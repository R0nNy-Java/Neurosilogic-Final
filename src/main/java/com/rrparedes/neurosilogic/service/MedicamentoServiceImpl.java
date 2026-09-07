package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Medicamento;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedicamentoServiceImpl implements MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final AuditoriaAccesoService auditoriaAccesoService;

    public MedicamentoServiceImpl(MedicamentoRepository medicamentoRepository, AuditoriaAccesoService auditoriaAccesoService) {
        this.medicamentoRepository = medicamentoRepository;
        this.auditoriaAccesoService = auditoriaAccesoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medicamento> listarTodos() {
        return medicamentoRepository.findAll();
    }

    @Override
    public Medicamento registrar(Usuario actor, String nombre, String composicion, String dosisRecomendada, Integer stock) {
        Medicamento med = new Medicamento(nombre, composicion, dosisRecomendada, stock != null ? stock : 0);
        Medicamento guardado = medicamentoRepository.save(med);
        auditoriaAccesoService.registrar(actor, "ALTA_MEDICAMENTO_CATALOGO", "Medicamento: " + nombre + " (Stock: " + med.getStock() + ")");
        return guardado;
    }

    @Override
    public void eliminar(Usuario actor, Long idMedicamento) {
        Medicamento med = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new NegocioException("El medicamento no existe o ya fue eliminado."));
        medicamentoRepository.delete(med);
        auditoriaAccesoService.registrar(actor, "BAJA_MEDICAMENTO_CATALOGO", "Medicamento: " + med.getNombreMedicamento());
    }

    @Override
    public Medicamento agregarStock(Usuario actor, Long idMedicamento, Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new NegocioException("La cantidad a agregar debe ser mayor a 0.");
        }
        Medicamento med = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new NegocioException("El medicamento no existe o ya fue eliminado."));

        int stockActual = med.getStock();
        long nuevoStock = (long) stockActual + cantidad;
        if (nuevoStock > 100000) {
            throw new NegocioException("El stock resultante no puede superar 100000 unidades.");
        }

        med.setStock((int) nuevoStock);
        Medicamento guardado = medicamentoRepository.save(med);
        auditoriaAccesoService.registrar(actor, "REABASTECIMIENTO_MEDICAMENTO",
                "Medicamento: " + med.getNombreMedicamento() + " (+" + cantidad + ", nuevo stock: " + nuevoStock + ")");
        return guardado;
    }

    @Override
    public Medicamento editar(Usuario actor, Long idMedicamento, String nombre, String composicion, String dosisRecomendada, Integer stock) {
        Medicamento med = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new NegocioException("El medicamento no existe o ya fue eliminado."));

        if (nombre == null || nombre.trim().isBlank()) {
            throw new NegocioException("El nombre del medicamento es obligatorio.");
        }
        if (stock != null && stock < 0) {
            throw new NegocioException("El stock no puede ser negativo.");
        }

        med.setNombreMedicamento(nombre.trim());
        med.setUnidadConcentracion(composicion);
        med.setPresentacionCompleta(dosisRecomendada);
        med.setStock(stock != null ? stock : med.getStock());
        Medicamento guardado = medicamentoRepository.save(med);
        auditoriaAccesoService.registrar(actor, "EDICION_MEDICAMENTO_CATALOGO", "Medicamento: " + med.getNombreMedicamento());
        return guardado;
    }
}
