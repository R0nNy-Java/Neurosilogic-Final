package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "alerta_clinica")
public class AlertaClinica implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAlerta")
    private Long idAlerta;

    @Column(name = "Modulo", length = 50, nullable = false)
    private String modulo;

    @Column(name = "NivelAlerta", length = 50, nullable = false)
    private String nivelAlerta;

    @Column(name = "MensajeAlerta", length = 255, nullable = false)
    private String mensajeAlerta;

    @Column(name = "ColorCodigo", length = 20)
    private String colorCodigo;

    public AlertaClinica() {}

    public AlertaClinica(Long idAlerta, String modulo, String nivelAlerta, String mensajeAlerta, String colorCodigo) {
        this.idAlerta = idAlerta;
        this.modulo = modulo;
        this.nivelAlerta = nivelAlerta;
        this.mensajeAlerta = mensajeAlerta;
        this.colorCodigo = colorCodigo;
    }

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getNivelAlerta() { return nivelAlerta; }
    public void setNivelAlerta(String nivelAlerta) { this.nivelAlerta = nivelAlerta; }

    public String getMensajeAlerta() { return mensajeAlerta; }
    public void setMensajeAlerta(String mensajeAlerta) { this.mensajeAlerta = mensajeAlerta; }

    public String getColorCodigo() { return colorCodigo != null ? colorCodigo : "secondary"; }
    public void setColorCodigo(String colorCodigo) { this.colorCodigo = colorCodigo; }
}
