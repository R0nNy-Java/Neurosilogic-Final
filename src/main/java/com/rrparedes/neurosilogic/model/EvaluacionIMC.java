package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluacionimc")
public class EvaluacionIMC implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdIMC")
    private Long idIMC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente")
    private Paciente paciente;

    @Column(name = "IdPacienteTemp")
    private Long idPaciente;

    @Column(name = "Peso")
    private Double peso;

    @Column(name = "Estatura")
    private Double estatura;

    @Column(name = "ValorIMC")
    private Double valorIMC;

    @Column(name = "Clasificacion", length = 50)
    private String clasificacion;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public EvaluacionIMC() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getIdIMC() {
        return idIMC;
    }

    public void setIdIMC(Long idIMC) {
        this.idIMC = idIMC;
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

    public Double getPeso() {
        return peso;
    }

    public Double getPesoKg() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
        calcularIMC();
    }

    public void setPesoKg(Double pesoKg) {
        setPeso(pesoKg);
    }

    public Double getEstatura() {
        return estatura;
    }

    public Double getEstaturaM() {
        return estatura;
    }

    public void setEstatura(Double estatura) {
        this.estatura = estatura;
        calcularIMC();
    }

    public void setEstaturaM(Double estaturaM) {
        setEstatura(estaturaM);
    }

    public Double getValorIMC() {
        if (valorIMC == null) calcularIMC();
        return valorIMC;
    }

    public void setValorIMC(Double valorIMC) {
        this.valorIMC = valorIMC;
    }

    private void calcularIMC() {
        if (peso != null && estatura != null && estatura > 0) {
            this.valorIMC = peso / (estatura * estatura);
            if (this.valorIMC < 18.5) this.clasificacion = "Bajo peso";
            else if (this.valorIMC < 25.0) this.clasificacion = "Peso normal";
            else if (this.valorIMC < 30.0) this.clasificacion = "Sobrepeso";
            else this.clasificacion = "Obesidad";
        }
    }

    public String getClasificacion() {
        if (clasificacion == null) calcularIMC();
        return clasificacion;
    }

    public String getDiagnostico() {
        return getClasificacion();
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
