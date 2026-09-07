package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.SignoVital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SignoVitalRepository extends JpaRepository<SignoVital, Long> {
    List<SignoVital> findByPacienteIdPaciente(Long idPaciente);
    long countByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);
}
