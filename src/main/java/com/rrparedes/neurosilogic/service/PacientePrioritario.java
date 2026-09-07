package com.rrparedes.neurosilogic.service;

/**
 * Un paciente con alertas activas dentro del período, para el listado de
 * "Pacientes que Requieren Atención" que ayuda a priorizar al cierre de turno.
 *
 * Deliberadamente NO guarda la entidad {@code Paciente} completa: esta vista se serializa
 * también a JSON para alimentar los gráficos (Thymeleaf JS inlining), y una entidad JPA con
 * relaciones @ManyToOne perezosas no se puede serializar de forma segura con Jackson.
 */
public record PacientePrioritario(
        Long idPaciente,
        String nombreCompleto,
        String cedula,
        long totalAlertas,
        boolean tieneAlertaCritica,
        String ultimoMensaje,
        String ultimoModulo
) {}
