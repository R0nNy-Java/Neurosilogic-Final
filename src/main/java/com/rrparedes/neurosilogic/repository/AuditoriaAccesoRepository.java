package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.AuditoriaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaAccesoRepository extends JpaRepository<AuditoriaAcceso, Long> {
    List<AuditoriaAcceso> findAllByOrderByTimestampDesc();
}
