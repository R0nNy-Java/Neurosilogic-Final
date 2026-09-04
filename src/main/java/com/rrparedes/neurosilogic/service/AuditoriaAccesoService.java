package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AuditoriaAcceso;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.AuditoriaAccesoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaAccesoService {

    private final AuditoriaAccesoRepository auditoriaAccesoRepository;

    public AuditoriaAccesoService(AuditoriaAccesoRepository auditoriaAccesoRepository) {
        this.auditoriaAccesoRepository = auditoriaAccesoRepository;
    }

    public void registrar(Usuario usuario, Paciente paciente, String accion, String detalle) {
        auditoriaAccesoRepository.save(new AuditoriaAcceso(usuario, paciente, accion, detalle, LocalDateTime.now()));
    }

    public void registrar(Usuario usuario, String accion) {
        registrar(usuario, null, accion, null);
    }

    public void registrar(Usuario usuario, String accion, String detalle) {
        registrar(usuario, null, accion, detalle);
    }
}
