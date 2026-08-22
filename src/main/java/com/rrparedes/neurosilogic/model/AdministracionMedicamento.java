package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "administracionmedicamento")
public class AdministracionMedicamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAdministracion")
    private Long idAdministracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPrescripcion")
    private PrescripcionMedica prescripcionMedica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    @Column(name = "DosisCalculada", precision = 10, scale = 2)
    private BigDecimal dosisCalculada;

    @Column(name = "UnidadResultado", length = 255)
    private String unidadResultado;

    @Column(name = "HoraAdministracion")
    private LocalDateTime horaAdministracion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEnfermero")
    private Usuario enfermero;

    public AdministracionMedicamento() {
    }

    public AdministracionMedicamento(PrescripcionMedica prescripcionMedica, Paciente paciente, BigDecimal dosisCalculada, String unidadResultado, LocalDateTime horaAdministracion, Usuario enfermero) {
        this.prescripcionMedica = prescripcionMedica;
        this.paciente = paciente;
        this.dosisCalculada = dosisCalculada;
        this.unidadResultado = unidadResultado;
        this.horaAdministracion = horaAdministracion;
        this.enfermero = enfermero;
    }

    public Long getIdAdministracion() {
        return idAdministracion;
    }

    public void setIdAdministracion(Long idAdministracion) {
        this.idAdministracion = idAdministracion;
    }

    public PrescripcionMedica getPrescripcionMedica() {
        return prescripcionMedica;
    }

    public void setPrescripcionMedica(PrescripcionMedica prescripcionMedica) {
        this.prescripcionMedica = prescripcionMedica;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public BigDecimal getDosisCalculada() {
        return dosisCalculada;
    }

    public void setDosisCalculada(BigDecimal dosisCalculada) {
        this.dosisCalculada = dosisCalculada;
    }

    public String getUnidadResultado() {
        return unidadResultado;
    }

    public void setUnidadResultado(String unidadResultado) {
        this.unidadResultado = unidadResultado;
    }

    public LocalDateTime getHoraAdministracion() {
        return horaAdministracion;
    }

    public void setHoraAdministracion(LocalDateTime horaAdministracion) {
        this.horaAdministracion = horaAdministracion;
    }

    public Usuario getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Usuario enfermero) {
        this.enfermero = enfermero;
    }
}
