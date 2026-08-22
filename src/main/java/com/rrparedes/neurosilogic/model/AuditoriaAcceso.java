package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoriaacceso")
public class AuditoriaAcceso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAuditoria")
    private Long idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente")
    private Paciente paciente;

    @Column(name = "Accion", length = 50)
    private String accion;

    @Column(name = "MotivoAnulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @Column(name = "Timestamp")
    private LocalDateTime timestamp;

    public AuditoriaAcceso() {
    }

    public AuditoriaAcceso(Usuario usuario, Paciente paciente, String accion, String motivoAnulacion, LocalDateTime timestamp) {
        this.usuario = usuario;
        this.paciente = paciente;
        this.accion = accion;
        this.motivoAnulacion = motivoAnulacion;
        this.timestamp = timestamp;
    }

    public Long getIdAuditoria() {
        return idAuditoria;
    }

    public void setIdAuditoria(Long idAuditoria) {
        this.idAuditoria = idAuditoria;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
