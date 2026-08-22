package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prescripcionmedica")
public class PrescripcionMedica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPrescripcion")
    private Long idPrescripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdMedicamento", nullable = false)
    private Medicamento medicamento;

    @Column(name = "DosisPrescrita", precision = 10, scale = 2)
    private BigDecimal dosisPrescrita;

    @Column(name = "UnidadDosis", length = 10)
    private String unidadDosis;

    @Column(name = "FechaPrescripcion")
    private LocalDateTime fechaPrescripcion;

    public PrescripcionMedica() {
    }

    public PrescripcionMedica(Paciente paciente, Medicamento medicamento, BigDecimal dosisPrescrita, String unidadDosis, LocalDateTime fechaPrescripcion) {
        this.paciente = paciente;
        this.medicamento = medicamento;
        this.dosisPrescrita = dosisPrescrita;
        this.unidadDosis = unidadDosis;
        this.fechaPrescripcion = fechaPrescripcion;
    }

    public Long getIdPrescripcion() {
        return idPrescripcion;
    }

    public void setIdPrescripcion(Long idPrescripcion) {
        this.idPrescripcion = idPrescripcion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public BigDecimal getDosisPrescrita() {
        return dosisPrescrita;
    }

    public void setDosisPrescrita(BigDecimal dosisPrescrita) {
        this.dosisPrescrita = dosisPrescrita;
    }

    public String getUnidadDosis() {
        return unidadDosis;
    }

    public void setUnidadDosis(String unidadDosis) {
        this.unidadDosis = unidadDosis;
    }

    public LocalDateTime getFechaPrescripcion() {
        return fechaPrescripcion;
    }

    public void setFechaPrescripcion(LocalDateTime fechaPrescripcion) {
        this.fechaPrescripcion = fechaPrescripcion;
    }
}
