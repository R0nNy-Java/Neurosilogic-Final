package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "signovital")
public class SignoVital implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdSignoVital")
    private Long idSignoVital;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    @Column(name = "FechaHora")
    private LocalDateTime fechaHora;

    @DecimalMin(value = "25.0", message = "La temperatura no puede ser menor a 25.0°C")
    @DecimalMax(value = "45.0", message = "La temperatura no puede ser mayor a 45.0°C")
    @Column(name = "Temperatura", precision = 4, scale = 2)
    private BigDecimal temperatura;

    @Range(min = 30, max = 250, message = "La presión sistólica debe estar entre 30 y 250 mmHg")
    @Column(name = "PresionSistolica")
    private Integer presionSistolica;

    @Range(min = 30, max = 250, message = "La presión diastólica debe estar entre 30 y 250 mmHg")
    @Column(name = "PresionDiastolica")
    private Integer presionDiastolica;

    @Range(min = 20, max = 250, message = "La frecuencia cardiaca debe estar entre 20 y 250 lpm")
    @Column(name = "FrecuenciaCardiaca")
    private Integer frecuenciaCardiaca;

    @Range(min = 5, max = 60, message = "La frecuencia respiratoria debe estar entre 5 y 60 rpm")
    @Column(name = "FrecuenciaRespiratoria")
    private Integer frecuenciaRespiratoria;

    @Range(min = 0, max = 100, message = "La saturación de oxígeno debe estar entre 0 y 100%")
    @Column(name = "SaturacionO2")
    private Integer saturacionO2;

    @Column(name = "Glicemia", precision = 5, scale = 1)
    private BigDecimal glicemia;

    @Column(name = "AlertaGenerada", length = 1)
    private String alertaGenerada;

    public SignoVital() {
        this.fechaHora = LocalDateTime.now();
    }

    public Long getIdSignoVital() {
        return idSignoVital;
    }

    public void setIdSignoVital(Long idSignoVital) {
        this.idSignoVital = idSignoVital;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora != null ? fechaHora : LocalDateTime.now();
    }

    public LocalDateTime getFechaRegistro() {
        return getFechaHora();
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public BigDecimal getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(BigDecimal temperatura) {
        this.temperatura = temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura != null ? BigDecimal.valueOf(temperatura) : null;
    }

    public Integer getPresionSistolica() {
        return presionSistolica;
    }

    public void setPresionSistolica(Integer presionSistolica) {
        this.presionSistolica = presionSistolica;
    }

    public Integer getPresionDiastolica() {
        return presionDiastolica;
    }

    public void setPresionDiastolica(Integer presionDiastolica) {
        this.presionDiastolica = presionDiastolica;
    }

    public String getPresionArterial() {
        if (presionSistolica == null || presionDiastolica == null) return "120/80";
        return presionSistolica + "/" + presionDiastolica;
    }

    public void setPresionArterial(String pa) {
        if (pa != null && pa.contains("/")) {
            String[] partes = pa.split("/");
            try {
                this.presionSistolica = Integer.parseInt(partes[0].trim());
                this.presionDiastolica = Integer.parseInt(partes[1].trim());
            } catch (Exception ignored) {}
        }
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public Integer getSaturacionO2() {
        return saturacionO2;
    }

    public Integer getSaturacionOxigeno() {
        return saturacionO2;
    }

    public void setSaturacionO2(Integer saturacionO2) {
        this.saturacionO2 = saturacionO2;
    }

    public void setSaturacionOxigeno(Integer saturacionOxigeno) {
        this.saturacionO2 = saturacionOxigeno;
    }

    public BigDecimal getGlicemia() {
        return glicemia;
    }

    public void setGlicemia(BigDecimal glicemia) {
        this.glicemia = glicemia;
    }

    public String getAlertaGenerada() {
        return alertaGenerada;
    }

    public void setAlertaGenerada(String alertaGenerada) {
        this.alertaGenerada = alertaGenerada;
    }
}
