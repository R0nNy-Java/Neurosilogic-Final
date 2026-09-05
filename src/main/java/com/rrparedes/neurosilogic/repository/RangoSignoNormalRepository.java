package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.RangoSignoNormal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RangoSignoNormalRepository extends JpaRepository<RangoSignoNormal, Long> {
    Optional<RangoSignoNormal> findByIdEnfermedad(Long idEnfermedad);
    Optional<RangoSignoNormal> findByIdEnfermedadIsNull();
}
