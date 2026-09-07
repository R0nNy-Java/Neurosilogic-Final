package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Dosificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DosificacionRepository extends JpaRepository<Dosificacion, Long> {
    List<Dosificacion> findByPacienteIdPacienteOrderByFechaRegistroDesc(Long idPaciente);
    long countByFechaRegistroBetween(LocalDateTime inicio, LocalDateTime fin);
}
