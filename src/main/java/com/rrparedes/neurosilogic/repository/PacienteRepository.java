package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
}
