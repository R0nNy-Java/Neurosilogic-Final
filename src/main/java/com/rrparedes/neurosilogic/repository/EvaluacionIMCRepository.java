package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.EvaluacionIMC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionIMCRepository extends JpaRepository<EvaluacionIMC, Long> {
    List<EvaluacionIMC> findByPaciente_IdPaciente(Long idPaciente);
    List<EvaluacionIMC> findByIdPaciente(Long idPaciente);
}
