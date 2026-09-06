package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "dosificacion")
public class Dosificacion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDosificacion")
    private Long idDosificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    @Column(name = "Medicamento", length = 100)
    private String medicamento;

    @Positive(message = "La dosis indicada debe ser mayor a 0")
    @Column(name = "DosisIndicada")
    private Double dosisIndicada;

    @Column(name = "UnidadDosis", length = 10)
    private String unidadDosis;

    @Positive(message = "La presentación debe ser mayor a 0")
    @Column(name = "Presentacion")
    private Double presentacion;

    @Column(name = "UnidadPresentacion", length = 10)
    private String unidadPresentacion;

    @Positive(message = "El volumen del diluyente debe ser mayor a 0")
    @Column(name = "DiluyenteMl")
    private Double diluyenteMl;

    @Column(name = "HorasTotales")
    private Double horasTotales;

    @Column(name = "VolumenAdministrarMl")
    private Double volumenAdministrarMl;

    @Column(name = "GotasPorMinuto")
    private Double gotasPorMinuto;

    @Column(name = "MicrogotasPorMinuto")
    private Double microgotasPorMinuto;

    @Column(name = "MlPorHora")
    private Double mlPorHora;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public Dosificacion() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ── Lógica de cálculo clínico (reutilizable de forma estática) ──

    // Método para homologar unidades a miligramos (mg) como base estándar
    private static double convertirAMg(double valor, String unidad) {
        if (unidad == null) return valor;
        switch (unidad.toLowerCase()) {
            case "g":
                return valor * 1000.0;     // 1 gramo = 1000 mg
            case "mcg":
                return valor / 1000.0;     // 1000 mcg = 1 mg
            case "mg":
            default:
                return valor;
        }
    }

    // Módulo 1: Regla de tres clínica
    public static double calcularVolumenAdministrar(double dosisIndicada, String unidadDosis,
                                                    double presentacion, String unidadPresentacion,
                                                    double diluyenteMl) {
        // Convertimos ambas a mg para que la regla de tres funcione con peras y peras
        double dosisMg = convertirAMg(dosisIndicada, unidadDosis);
        double presentacionMg = convertirAMg(presentacion, unidadPresentacion);

        if (presentacionMg <= 0) return 0.0;

        // Fórmula básica: (Dosis Solicitada * Diluyente) / Presentación
        return (dosisMg * diluyenteMl) / presentacionMg;
    }

    // Módulo 2: Infusiones y macro/micro goteos
    public static double calcularGotasPorMinuto(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        // Fórmula: Volumen / (Horas * 3)
        return volumenTotalMl / (horasTotales * 3.0);
    }

    public static double calcularMicrogotasPorMinuto(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        // Fórmula: Volumen / Horas (ya que 1 gota = 3 microgotas)
        return volumenTotalMl / horasTotales;
    }

    public static double calcularMlPorHora(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        return volumenTotalMl / horasTotales;
    }

    // Recalcula todos los valores derivados a partir de los datos de entrada actuales
    private void recalcular() {
        if (dosisIndicada != null && presentacion != null && diluyenteMl != null) {
            this.volumenAdministrarMl = calcularVolumenAdministrar(
                    dosisIndicada, unidadDosis, presentacion, unidadPresentacion, diluyenteMl);
        }
        if (volumenAdministrarMl != null && horasTotales != null && horasTotales > 0) {
            this.gotasPorMinuto = calcularGotasPorMinuto(volumenAdministrarMl, horasTotales);
            this.microgotasPorMinuto = calcularMicrogotasPorMinuto(volumenAdministrarMl, horasTotales);
            this.mlPorHora = calcularMlPorHora(volumenAdministrarMl, horasTotales);
        }
    }

    // ── Getters / Setters ──

    public Long getIdDosificacion() {
        return idDosificacion;
    }

    public void setIdDosificacion(Long idDosificacion) {
        this.idDosificacion = idDosificacion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public Double getDosisIndicada() {
        return dosisIndicada;
    }

    public void setDosisIndicada(Double dosisIndicada) {
        this.dosisIndicada = dosisIndicada;
        recalcular();
    }

    public String getUnidadDosis() {
        return unidadDosis;
    }

    public void setUnidadDosis(String unidadDosis) {
        this.unidadDosis = unidadDosis;
        recalcular();
    }

    public Double getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(Double presentacion) {
        this.presentacion = presentacion;
        recalcular();
    }

    public String getUnidadPresentacion() {
        return unidadPresentacion;
    }

    public void setUnidadPresentacion(String unidadPresentacion) {
        this.unidadPresentacion = unidadPresentacion;
        recalcular();
    }

    public Double getDiluyenteMl() {
        return diluyenteMl;
    }

    public void setDiluyenteMl(Double diluyenteMl) {
        this.diluyenteMl = diluyenteMl;
        recalcular();
    }

    public Double getHorasTotales() {
        return horasTotales;
    }

    public void setHorasTotales(Double horasTotales) {
        this.horasTotales = horasTotales;
        recalcular();
    }

    public Double getVolumenAdministrarMl() {
        return volumenAdministrarMl;
    }

    public void setVolumenAdministrarMl(Double volumenAdministrarMl) {
        this.volumenAdministrarMl = volumenAdministrarMl;
    }

    public Double getGotasPorMinuto() {
        return gotasPorMinuto;
    }

    public void setGotasPorMinuto(Double gotasPorMinuto) {
        this.gotasPorMinuto = gotasPorMinuto;
    }

    public Double getMicrogotasPorMinuto() {
        return microgotasPorMinuto;
    }

    public void setMicrogotasPorMinuto(Double microgotasPorMinuto) {
        this.microgotasPorMinuto = microgotasPorMinuto;
    }

    public Double getMlPorHora() {
        return mlPorHora;
    }

    public void setMlPorHora(Double mlPorHora) {
        this.mlPorHora = mlPorHora;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
