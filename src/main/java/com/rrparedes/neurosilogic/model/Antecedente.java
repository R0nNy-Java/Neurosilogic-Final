package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "antecedente")
public class Antecedente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAntecedente")
    private Long idAntecedente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente")
    private Paciente paciente;

    @Column(name = "IdPacienteTemp")
    private Long idPaciente;

    @Column(name = "Tipo", length = 50)
    private String tipo;

    @Column(name = "Alergias", length = 255)
    private String alergias;

    @Column(name = "MedicamentosActuales", length = 255)
    private String medicamentosActuales;

    @Column(name = "Observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public Antecedente() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getIdAntecedente() {
        return idAntecedente;
    }

    public void setIdAntecedente(Long idAntecedente) {
        this.idAntecedente = idAntecedente;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null) this.idPaciente = paciente.getIdPaciente();
    }

    public Long getIdPaciente() {
        if (idPaciente != null) return idPaciente;
        return paciente != null ? paciente.getIdPaciente() : null;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getTipo() {
        return tipo != null ? tipo : "Patológico";
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAlergias() {
        return alergias != null ? alergias : "Ninguna";
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getMedicamentosActuales() {
        return medicamentosActuales != null ? medicamentosActuales : "Ninguno";
    }

    public void setMedicamentosActuales(String medicamentosActuales) {
        this.medicamentosActuales = medicamentosActuales;
    }

    public String getObservacion() {
        return observacion;
    }

    public String getDescripcion() {
        return observacion != null ? observacion : alergias;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setDescripcion(String descripcion) {
        this.observacion = descripcion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
