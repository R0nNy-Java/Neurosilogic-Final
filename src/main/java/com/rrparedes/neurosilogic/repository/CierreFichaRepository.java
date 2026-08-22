package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.CierreFicha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CierreFichaRepository extends JpaRepository<CierreFicha, Long> {
    Optional<CierreFicha> findByPaciente_IdPaciente(Long idPaciente);
}
