package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.CierreFicha;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CierreFichaRepository extends JpaRepository<CierreFicha, Long> {
    Optional<CierreFicha> findTopByPaciente_IdPacienteOrderByFechaCierreDesc(Long idPaciente);
    java.util.List<CierreFicha> findAllByOrderByFechaCierreDesc();
    java.util.List<CierreFicha> findByEnfermeroIdUsuarioOrderByFechaCierreDesc(Long idUsuario);
}

