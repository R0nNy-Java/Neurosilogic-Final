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
    public Medicamento registrar(Usuario actor, String nombre, String composicion, String dosisRecomendada) {
        Medicamento med = new Medicamento(nombre, composicion, dosisRecomendada);
        Medicamento guardado = medicamentoRepository.save(med);
        auditoriaAccesoService.registrar(actor, "ALTA_MEDICAMENTO_CATALOGO", "Medicamento: " + nombre);
        return guardado;
    }
}
