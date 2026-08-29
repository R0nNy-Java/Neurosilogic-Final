package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "escalaglasgow")
public class EscalaGlasgow implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdGlasgow")
    private Long idGlasgow;

    @Column(name = "IdPaciente")
    private Long idPaciente;

    @Column(name = "FechaHora")
    private LocalDateTime fechaHora;

    @Column(name = "RespuestaOcular")
    private Integer respuestaOcular;

    @Column(name = "RespuestaVerbal")
    private Integer respuestaVerbal;

    @Column(name = "RespuestaMotora")
    private Integer respuestaMotora;

    @Column(name = "PuntajeTotal")
    private Integer puntajeTotal;

    @Column(name = "NivelSeveridad", length = 50)
    private String nivelSeveridad;

    @Column(name = "Observacion", length = 500)
    private String observacion;

    public EscalaGlasgow() {
        this.fechaHora = LocalDateTime.now();
    }

    public Long getIdGlasgow() {
        return idGlasgow;
    }

    public void setIdGlasgow(Long idGlasgow) {
        this.idGlasgow = idGlasgow;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
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

    public Integer getRespuestaOcular() {
        return respuestaOcular;
    }

    public void setRespuestaOcular(Integer respuestaOcular) {
        this.respuestaOcular = respuestaOcular;
        calcularPuntaje();
    }

    public Integer getRespuestaVerbal() {
        return respuestaVerbal;
    }

    public void setRespuestaVerbal(Integer respuestaVerbal) {
        this.respuestaVerbal = respuestaVerbal;
        calcularPuntaje();
    }

    public Integer getRespuestaMotora() {
        return respuestaMotora;
    }

    public void setRespuestaMotora(Integer respuestaMotora) {
        this.respuestaMotora = respuestaMotora;
        calcularPuntaje();
    }

    public Integer getPuntajeTotal() {
        if (puntajeTotal == null) calcularPuntaje();
        return puntajeTotal;
    }

    public void setPuntajeTotal(Integer puntajeTotal) {
        this.puntajeTotal = puntajeTotal;
    }

    private void calcularPuntaje() {
        int o = respuestaOcular != null ? respuestaOcular : 0;
        int v = respuestaVerbal != null ? respuestaVerbal : 0;
        int m = respuestaMotora != null ? respuestaMotora : 0;
        this.puntajeTotal = o + v + m;
        if (this.puntajeTotal >= 13) this.nivelSeveridad = "Leve / Normal";
        else if (this.puntajeTotal >= 9) this.nivelSeveridad = "Moderado";
        else this.nivelSeveridad = "Grave";
    }

    public String getNivelSeveridad() {
        if (nivelSeveridad == null) calcularPuntaje();
        return nivelSeveridad;
    }

    public String getClasificacion() {
        return getNivelSeveridad();
    }

    public void setNivelSeveridad(String nivelSeveridad) {
        this.nivelSeveridad = nivelSeveridad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
