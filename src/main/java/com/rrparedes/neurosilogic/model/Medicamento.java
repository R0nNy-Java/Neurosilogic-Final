package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "medicamento")
public class Medicamento implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdMedicamento")
    private Long idMedicamento;

    @Column(name = "NombreMedicamento", length = 100, nullable = false)
    private String nombreMedicamento;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Max(value = 100000, message = "El stock no puede superar 100000 unidades")
    @Column(name = "Stock")
    private Integer stock = 0;

    @Column(name = "Concentracion", precision = 10, scale = 2)
    private BigDecimal concentracion;

    @Column(name = "UnidadConcentracion", length = 50)
    private String unidadConcentracion;

    @Column(name = "VolumenPresentacion", precision = 10, scale = 2)
    private BigDecimal volumenPresentacion;

    @Column(name = "UnidadVolumen", length = 50)
    private String unidadVolumen;

    @Column(name = "PresentacionCompleta", length = 255)
    private String presentacionCompleta;

    public Medicamento() {
    }

    public Medicamento(String nombreMedicamento, String unidadConcentracion, String presentacionCompleta) {
        this.nombreMedicamento = nombreMedicamento;
        this.unidadConcentracion = unidadConcentracion;
        this.presentacionCompleta = presentacionCompleta;
    }

    public Medicamento(String nombreMedicamento, String unidadConcentracion, String presentacionCompleta, Integer stock) {
        this.nombreMedicamento = nombreMedicamento;
        this.unidadConcentracion = unidadConcentracion;
        this.presentacionCompleta = presentacionCompleta;
        this.stock = stock;
    }

    public Medicamento(String nombreMedicamento, BigDecimal concentracion, String unidadConcentracion, BigDecimal volumenPresentacion, String unidadVolumen, String presentacionCompleta) {
        this.nombreMedicamento = nombreMedicamento;
        this.concentracion = concentracion;
        this.unidadConcentracion = unidadConcentracion;
        this.volumenPresentacion = volumenPresentacion;
        this.unidadVolumen = unidadVolumen;
        this.presentacionCompleta = presentacionCompleta;
    }

    public Long getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Long idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getNombreMedicamento() {
        return nombreMedicamento;
    }

    public void setNombreMedicamento(String nombreMedicamento) {
        this.nombreMedicamento = nombreMedicamento;
    }

    public String getNombre() {
        return nombreMedicamento;
    }

    public Integer getStock() {
        return stock != null ? stock : 0;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getComposicion() {
        return unidadConcentracion != null ? unidadConcentracion : "General";
    }

    public String getDosisRecomendada() {
        return presentacionCompleta != null ? presentacionCompleta : "Según indicación médica";
    }

    public BigDecimal getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(BigDecimal concentracion) {
        this.concentracion = concentracion;
    }

    public String getUnidadConcentracion() {
        return unidadConcentracion;
    }

    public void setUnidadConcentracion(String unidadConcentracion) {
        this.unidadConcentracion = unidadConcentracion;
    }

    public BigDecimal getVolumenPresentacion() {
        return volumenPresentacion;
    }

    public void setVolumenPresentacion(BigDecimal volumenPresentacion) {
        this.volumenPresentacion = volumenPresentacion;
    }

    public String getUnidadVolumen() {
        return unidadVolumen;
    }

    public void setUnidadVolumen(String unidadVolumen) {
        this.unidadVolumen = unidadVolumen;
    }

    public String getPresentacionCompleta() {
        return presentacionCompleta;
    }

    public void setPresentacionCompleta(String presentacionCompleta) {
        this.presentacionCompleta = presentacionCompleta;
    }
}
