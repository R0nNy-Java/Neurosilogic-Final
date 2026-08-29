package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.EscalaGlasgow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalaGlasgowRepository extends JpaRepository<EscalaGlasgow, Long> {
    List<EscalaGlasgow> findByIdPaciente(Long idPaciente);
}
