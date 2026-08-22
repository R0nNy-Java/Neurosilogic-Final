package com.rrparedes.neurosilogic.repository;

import com.rrparedes.neurosilogic.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
}
