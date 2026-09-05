package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Enfermedad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnfermedadRepository extends JpaRepository<Enfermedad, Long> {
    Optional<Enfermedad> findByNombreEnfermedadIgnoreCase(String nombreEnfermedad);
}
