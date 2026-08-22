package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.SignoVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignoVitalRepository extends JpaRepository<SignoVital, Long> {
    List<SignoVital> findByPaciente_IdPaciente(Long idPaciente);
    List<SignoVital> findByIdPaciente(Long idPaciente);
}
