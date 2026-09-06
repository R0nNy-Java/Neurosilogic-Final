package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.AlertaClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaClinicaRepository extends JpaRepository<AlertaClinica, Long> {
    List<AlertaClinica> findByPacienteIdPacienteOrderByFechaRegistroDesc(Long idPaciente);
    void deleteByPacienteIdPacienteAndModulo(Long idPaciente, String modulo);
    List<AlertaClinica> findAllByOrderByFechaRegistroDesc();
    List<AlertaClinica> findByFechaRegistroBetweenOrderByFechaRegistroDesc(java.time.LocalDateTime inicio, java.time.LocalDateTime fin);
}
