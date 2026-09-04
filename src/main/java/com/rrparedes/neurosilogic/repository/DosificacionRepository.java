package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Dosificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DosificacionRepository extends JpaRepository<Dosificacion, Long> {
    List<Dosificacion> findByIdPacienteOrderByFechaRegistroDesc(Long idPaciente);
}
