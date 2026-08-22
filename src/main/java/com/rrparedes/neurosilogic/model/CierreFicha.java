package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierre_ficha")
public class CierreFicha implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdCierre")
    private Long idCierre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEnfermero")
    private Usuario enfermero;

    @Column(name = "NombreEnfermero", length = 150)
    private String nombreEnfermero;

    @Column(name = "FechaCierre")
    private LocalDateTime fechaCierre;

    @Column(name = "UltimosSignosVitales", length = 255)
    private String ultimosSignosVitales;

    @Column(name = "UltimoGlasgow", length = 255)
    private String ultimoGlasgow;

    @Column(name = "UltimoIMC", length = 255)
    private String ultimoIMC;

    @Column(name = "UltimosAntecedentes", length = 255)
    private String ultimosAntecedentes;

    @Column(name = "UltimaDosis", length = 255)
    private String ultimaDosis;

    public CierreFicha() {
        this.fechaCierre = LocalDateTime.now();
    }

    public CierreFicha(Paciente paciente, Usuario enfermero, String nombreEnfermero, String ultimosSignosVitales, String ultimoGlasgow, String ultimoIMC, String ultimosAntecedentes, String ultimaDosis) {
        this.paciente = paciente;
        this.enfermero = enfermero;
        this.nombreEnfermero = nombreEnfermero;
        this.ultimosSignosVitales = ultimosSignosVitales;
        this.ultimoGlasgow = ultimoGlasgow;
        this.ultimoIMC = ultimoIMC;
        this.ultimosAntecedentes = ultimosAntecedentes;
        this.ultimaDosis = ultimaDosis;
        this.fechaCierre = LocalDateTime.now();
    }

    public Long getIdCierre() {
        return idCierre;
    }

    public void setIdCierre(Long idCierre) {
        this.idCierre = idCierre;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Usuario getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Usuario enfermero) {
        this.enfermero = enfermero;
    }

    public String getNombreEnfermero() {
        return nombreEnfermero;
    }

    public void setNombreEnfermero(String nombreEnfermero) {
        this.nombreEnfermero = nombreEnfermero;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getUltimosSignosVitales() {
        return ultimosSignosVitales != null ? ultimosSignosVitales : "Sin registro de signos vitales.";
    }

    public void setUltimosSignosVitales(String ultimosSignosVitales) {
        this.ultimosSignosVitales = ultimosSignosVitales;
    }

    public String getUltimoGlasgow() {
        return ultimoGlasgow != null ? ultimoGlasgow : "Sin evaluación de Glasgow.";
    }

    public void setUltimoGlasgow(String ultimoGlasgow) {
        this.ultimoGlasgow = ultimoGlasgow;
    }

    public String getUltimoIMC() {
        return ultimoIMC != null ? ultimoIMC : "Sin evaluación de IMC.";
    }

    public void setUltimoIMC(String ultimoIMC) {
        this.ultimoIMC = ultimoIMC;
    }

    public String getUltimosAntecedentes() {
        return ultimosAntecedentes != null ? ultimosAntecedentes : "Sin antecedentes registrados.";
    }

    public void setUltimosAntecedentes(String ultimosAntecedentes) {
        this.ultimosAntecedentes = ultimosAntecedentes;
    }

    public String getUltimaDosis() {
        return ultimaDosis != null ? ultimaDosis : "Sin administración de dosis registrada.";
    }

    public void setUltimaDosis(String ultimaDosis) {
        this.ultimaDosis = ultimaDosis;
    }
}
