package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.AlertaClinica;
import com.rrparedes.neurosilogic.model.AuditoriaAcceso;
import com.rrparedes.neurosilogic.model.CierreFicha;
import com.rrparedes.neurosilogic.model.Usuario;
import com.rrparedes.neurosilogic.repository.AlertaClinicaRepository;
import com.rrparedes.neurosilogic.repository.AuditoriaAccesoRepository;
import com.rrparedes.neurosilogic.repository.CierreFichaRepository;
import com.rrparedes.neurosilogic.repository.DosificacionRepository;
import com.rrparedes.neurosilogic.repository.EscalaGlasgowRepository;
import com.rrparedes.neurosilogic.repository.EvaluacionIMCRepository;
import com.rrparedes.neurosilogic.repository.SignoVitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("dd/MM");

    private final AuditoriaAccesoRepository auditoriaAccesoRepository;
    private final AlertaClinicaRepository alertaClinicaRepository;
    private final CierreFichaRepository cierreFichaRepository;
    private final SignoVitalRepository signoVitalRepository;
    private final EscalaGlasgowRepository escalaGlasgowRepository;
    private final EvaluacionIMCRepository evaluacionIMCRepository;
    private final DosificacionRepository dosificacionRepository;
    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    public ReporteServiceImpl(AuditoriaAccesoRepository auditoriaAccesoRepository,
                              AlertaClinicaRepository alertaClinicaRepository,
                              CierreFichaRepository cierreFichaRepository,
                              SignoVitalRepository signoVitalRepository,
                              EscalaGlasgowRepository escalaGlasgowRepository,
                              EvaluacionIMCRepository evaluacionIMCRepository,
                              DosificacionRepository dosificacionRepository,
                              PacienteService pacienteService,
                              UsuarioService usuarioService) {
        this.auditoriaAccesoRepository = auditoriaAccesoRepository;
        this.alertaClinicaRepository = alertaClinicaRepository;
        this.cierreFichaRepository = cierreFichaRepository;
        this.signoVitalRepository = signoVitalRepository;
        this.escalaGlasgowRepository = escalaGlasgowRepository;
        this.evaluacionIMCRepository = evaluacionIMCRepository;
        this.dosificacionRepository = dosificacionRepository;
        this.pacienteService = pacienteService;
        this.usuarioService = usuarioService;
    }

    @Override
    public List<AuditoriaAcceso> obtenerUltimosMovimientos() {
        return auditoriaAccesoRepository.findAllByOrderByTimestampDesc();
    }

    @Override
    public ReporteResumenPeriodo generarReportePeriodo(Usuario usuarioLogueado, LocalDateTime inicio, LocalDateTime fin) {
        boolean conRango = inicio != null && fin != null;

        // Alertas del período: son la base tanto de la bitácora del admin como del panel de
        // "pacientes que requieren atención" que se muestra a cualquier rol.
        List<AlertaClinica> alertasPeriodo = conRango
                ? alertaClinicaRepository.findByFechaRegistroBetweenOrderByFechaRegistroDesc(inicio, fin)
                : alertaClinicaRepository.findAllByOrderByFechaRegistroDesc();

        long registrosClinicos = contarRegistrosClinicos(inicio, fin, conRango);

        if (usuarioLogueado.isAdministrador()) {
            List<CierreFicha> cierres = conRango
                    ? cierreFichaRepository.findByFechaCierreBetweenOrderByFechaCierreDesc(inicio, fin)
                    : cierreFichaRepository.findAllByOrderByFechaCierreDesc();

            ReporteDecisionData decision = construirDecisionData(alertasPeriodo, registrosClinicos, cierres.size());

            return new ReporteResumenPeriodo(
                    cierres,
                    alertasPeriodo,
                    auditoriaAccesoRepository.findAllByOrderByTimestampDesc(),
                    usuarioService.listarTodos(),
                    pacienteService.listarTodos(),
                    null,
                    decision
            );
        }

        List<CierreFicha> misCierres = conRango
                ? cierreFichaRepository.findByEnfermeroIdUsuarioAndFechaCierreBetweenOrderByFechaCierreDesc(usuarioLogueado.getIdUsuario(), inicio, fin)
                : pacienteService.obtenerCierresFichaPorEnfermero(usuarioLogueado.getIdUsuario());

        ReporteDecisionData decision = construirDecisionData(alertasPeriodo, registrosClinicos, misCierres.size());

        return new ReporteResumenPeriodo(
                null,
                null,
                null,
                null,
                pacienteService.listarTodos(),
                misCierres,
                decision
        );
    }

    private long contarRegistrosClinicos(LocalDateTime inicio, LocalDateTime fin, boolean conRango) {
        if (conRango) {
            return signoVitalRepository.countByFechaHoraBetween(inicio, fin)
                    + escalaGlasgowRepository.countByFechaHoraBetween(inicio, fin)
                    + evaluacionIMCRepository.countByFechaRegistroBetween(inicio, fin)
                    + dosificacionRepository.countByFechaRegistroBetween(inicio, fin);
        }
        return signoVitalRepository.count() + escalaGlasgowRepository.count()
                + evaluacionIMCRepository.count() + dosificacionRepository.count();
    }

    /** Construye las métricas de apoyo a decisiones a partir de las alertas vigentes del período. */
    private ReporteDecisionData construirDecisionData(List<AlertaClinica> alertas, long registrosClinicos, long cierresFicha) {
        long criticas = alertas.stream().filter(a -> "danger".equalsIgnoreCase(a.getColorCodigo())).count();
        long advertencias = alertas.size() - criticas;

        Map<String, Long> porModulo = new LinkedHashMap<>();
        for (AlertaClinica a : alertas) {
            porModulo.merge(a.getModulo() != null ? a.getModulo() : "Otro", 1L, Long::sum);
        }
        List<ConteoEtiqueta> alertasPorModulo = porModulo.entrySet().stream()
                .map(e -> new ConteoEtiqueta(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        Map<LocalDate, Long> porDia = new TreeMap<>();
        for (AlertaClinica a : alertas) {
            if (a.getFechaRegistro() != null) {
                porDia.merge(a.getFechaRegistro().toLocalDate(), 1L, Long::sum);
            }
        }
        List<ConteoEtiqueta> tendenciaDiaria = porDia.entrySet().stream()
                .map(e -> new ConteoEtiqueta(e.getKey().format(FORMATO_DIA), e.getValue()))
                .collect(Collectors.toList());

        Map<Long, List<AlertaClinica>> porPaciente = alertas.stream()
                .filter(a -> a.getPaciente() != null)
                .collect(Collectors.groupingBy(a -> a.getPaciente().getIdPaciente(), LinkedHashMap::new, Collectors.toList()));

        List<PacientePrioritario> prioritarios = porPaciente.values().stream()
                .map(lista -> {
                    AlertaClinica masReciente = lista.stream()
                            .max(Comparator.comparing(AlertaClinica::getFechaRegistro))
                            .orElse(lista.get(0));
                    boolean tieneCritica = lista.stream().anyMatch(a -> "danger".equalsIgnoreCase(a.getColorCodigo()));
                    var paciente = masReciente.getPaciente();
                    return new PacientePrioritario(paciente.getIdPaciente(),
                            paciente.getNombres() + " " + paciente.getApellidos(), paciente.getCedula(),
                            lista.size(), tieneCritica, masReciente.getMensajeAlerta(), masReciente.getModulo());
                })
                .sorted(Comparator.comparing(PacientePrioritario::tieneAlertaCritica).reversed()
                        .thenComparing(PacientePrioritario::totalAlertas, Comparator.reverseOrder()))
                .limit(8)
                .collect(Collectors.toList());

        return new ReporteDecisionData(alertas.size(), criticas, advertencias, registrosClinicos, cierresFicha,
                alertasPorModulo, tendenciaDiaria, prioritarios);
    }
}
