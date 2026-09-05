package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "rango_signo_normal")
public class RangoSignoNormal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdRango")
    private Long idRango;

    @Column(name = "IdEnfermedad")
    private Long idEnfermedad; // null para persona sana estándar

    @Column(name = "NombreParametro", length = 100)
    private String nombreParametro; // "Estándar Sano" o nombre de la patología

    @Column(name = "TempMin", precision = 4, scale = 2)
    private BigDecimal tempMin;

    @Column(name = "TempMax", precision = 4, scale = 2)
    private BigDecimal tempMax;

    @Column(name = "SisMin")
    private Integer sisMin;

    @Column(name = "SisMax")
    private Integer sisMax;

    @Column(name = "DiaMin")
    private Integer diaMin;

    @Column(name = "DiaMax")
    private Integer diaMax;

    @Column(name = "FcMin")
    private Integer fcMin;

    @Column(name = "FcMax")
    private Integer fcMax;

    @Column(name = "FrMin")
    private Integer frMin;

    @Column(name = "FrMax")
    private Integer frMax;

    @Column(name = "SatMin")
    private Integer satMin;

    @Column(name = "SatMax")
    private Integer satMax;

    public RangoSignoNormal() {
    }

    public RangoSignoNormal(Long idEnfermedad, String nombreParametro, Double tempMin, Double tempMax, Integer sisMin, Integer sisMax, Integer diaMin, Integer diaMax, Integer fcMin, Integer fcMax, Integer frMin, Integer frMax, Integer satMin, Integer satMax) {
        this.idEnfermedad = idEnfermedad;
        this.nombreParametro = nombreParametro;
        this.tempMin = tempMin != null ? BigDecimal.valueOf(tempMin) : null;
        this.tempMax = tempMax != null ? BigDecimal.valueOf(tempMax) : null;
        this.sisMin = sisMin;
        this.sisMax = sisMax;
        this.diaMin = diaMin;
        this.diaMax = diaMax;
        this.fcMin = fcMin;
        this.fcMax = fcMax;
        this.frMin = frMin;
        this.frMax = frMax;
        this.satMin = satMin;
        this.satMax = satMax;
    }

    public Long getIdRango() {
        return idRango;
    }

    public void setIdRango(Long idRango) {
        this.idRango = idRango;
    }

    public Long getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(Long idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public String getNombreParametro() {
        return nombreParametro;
    }

    public void setNombreParametro(String nombreParametro) {
        this.nombreParametro = nombreParametro;
    }

    public BigDecimal getTempMin() {
        return tempMin;
    }

    public void setTempMin(BigDecimal tempMin) {
        this.tempMin = tempMin;
    }

    public BigDecimal getTempMax() {
        return tempMax;
    }

    public void setTempMax(BigDecimal tempMax) {
        this.tempMax = tempMax;
    }

    public Integer getSisMin() {
        return sisMin;
    }

    public void setSisMin(Integer sisMin) {
        this.sisMin = sisMin;
    }

    public Integer getSisMax() {
        return sisMax;
    }

    public void setSisMax(Integer sisMax) {
        this.sisMax = sisMax;
    }

    public Integer getDiaMin() {
        return diaMin;
    }

    public void setDiaMin(Integer diaMin) {
        this.diaMin = diaMin;
    }

    public Integer getDiaMax() {
        return diaMax;
    }

    public void setDiaMax(Integer diaMax) {
        this.diaMax = diaMax;
    }

    public Integer getFcMin() {
        return fcMin;
    }

    public void setFcMin(Integer fcMin) {
        this.fcMin = fcMin;
    }

    public Integer getFcMax() {
        return fcMax;
    }

    public void setFcMax(Integer fcMax) {
        this.fcMax = fcMax;
    }

    public Integer getFrMin() {
        return frMin;
    }

    public void setFrMin(Integer frMin) {
        this.frMin = frMin;
    }

    public Integer getFrMax() {
        return frMax;
    }

    public void setFrMax(Integer frMax) {
        this.frMax = frMax;
    }

    public Integer getSatMin() {
        return satMin;
    }

    public void setSatMin(Integer satMin) {
        this.satMin = satMin;
    }

    public Integer getSatMax() {
        return satMax;
    }

    public void setSatMax(Integer satMax) {
        this.satMax = satMax;
    }
}
