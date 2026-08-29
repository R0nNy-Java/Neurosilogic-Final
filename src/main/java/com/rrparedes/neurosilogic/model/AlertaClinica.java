package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerta_clinica")
public class AlertaClinica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAlerta")
    private Long idAlerta;

    @Column(name = "IdPaciente")
    private Long idPaciente;

    @Column(name = "Modulo", length = 50, nullable = false)
    private String modulo;

    @Column(name = "NivelAlerta", length = 50, nullable = false)
    private String nivelAlerta;

    @Column(name = "MensajeAlerta", length = 255, nullable = false)
    private String mensajeAlerta;

    @Column(name = "ColorCodigo", length = 20)
    private String colorCodigo;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public AlertaClinica() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public AlertaClinica(Long idPaciente, String modulo, String nivelAlerta, String mensajeAlerta, String colorCodigo) {
        this.idPaciente = idPaciente;
        this.modulo = modulo;
        this.nivelAlerta = nivelAlerta;
        this.mensajeAlerta = mensajeAlerta;
        this.colorCodigo = colorCodigo;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro != null ? fechaRegistro : LocalDateTime.now(); }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; }

    public String getColorCodigo() { return colorCodigo != null ? colorCodigo : "secondary"; }
    public void setColorCodigo(String colorCodigo) { this.colorCodigo = colorCodigo; }
}
