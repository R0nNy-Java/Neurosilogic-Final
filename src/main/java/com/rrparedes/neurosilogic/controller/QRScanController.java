package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.model.CierreFicha;
import com.rrparedes.neurosilogic.model.Paciente;
import com.rrparedes.neurosilogic.repository.CierreFichaRepository;
import com.rrparedes.neurosilogic.service.PacienteService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class QRScanController {

    private final PacienteService pacienteService;
    private final CierreFichaRepository cierreFichaRepository;

    public QRScanController(PacienteService pacienteService, CierreFichaRepository cierreFichaRepository) {
        this.pacienteService = pacienteService;
        this.cierreFichaRepository = cierreFichaRepository;
    }

    /**
     * Endpoint de acceso público PWA para escanear el Código QR de la pulsera inteligente.
     * Muestra la vista Kanban (Cierre de Ficha) del paciente si se encuentra activo ('A'),
     * o despliega la tarjeta de alerta de inactividad / alta otorgada si estado != 'A'.
     */
    @GetMapping("/qr/scan")
    public String escaneoPulsera(@RequestParam(required = false) Long id,
                                @RequestParam(required = false) String cedula,
                                Model model) {
        Optional<Paciente> pacienteOpt = Optional.empty();

        if (id != null) {
            pacienteOpt = pacienteService.buscarPorId(id);
        } else if (cedula != null && !cedula.trim().isEmpty()) {
            pacienteOpt = pacienteService.buscarPorCedula(cedula.trim());
        }

        if (pacienteOpt.isEmpty()) {
            model.addAttribute("errorModal", "No se encontró información para el paciente consultado.");
            return "qr_kanban_view";
        }

        Paciente paciente = pacienteOpt.get();
        model.addAttribute("paciente", paciente);

        // Seguridad y Ciclo de Vida: Inactivación por Alta (estado != 'A')
        boolean esActivo = "A".equalsIgnoreCase(paciente.getEstado());
        model.addAttribute("esActivo", esActivo);

        if (!esActivo) {
            // Paciente inactivo o dado de alta -> Acceso Denegado / Tarjeta de Alerta
            return "qr_kanban_view";
        }

        // Paciente Activo ('A') -> Cargar datos del Kanban (Cierre de Ficha)
        pacienteService.obtenerPanel(paciente.getIdPaciente()).ifPresent(panel -> {
            model.addAttribute("panelData", panel);
        });

        Optional<CierreFicha> ultimoCierreOpt = cierreFichaRepository.findTopByPaciente_IdPacienteOrderByFechaCierreDesc(paciente.getIdPaciente());
        ultimoCierreOpt.ifPresent(cierre -> model.addAttribute("ultimoCierre", cierre));

        List<CierreFicha> historialCierres = cierreFichaRepository.findByPaciente_IdPacienteOrderByFechaCierreDesc(paciente.getIdPaciente());
        model.addAttribute("historialCierres", historialCierres);

        return "qr_kanban_view";
    }

    /**
     * Endpoint para lanzar la cámara de escaneo QR directa.
     */
    @GetMapping("/qr/camara")
    public String abrirEscanerCamara() {
        return "qr_kanban_view";
    }
}
