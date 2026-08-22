package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Antecedente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AntecedenteRepository extends JpaRepository<Antecedente, Long> {
    List<Antecedente> findByPaciente_IdPaciente(Long idPaciente);
    List<Antecedente> findByIdPaciente(Long idPaciente);
}
