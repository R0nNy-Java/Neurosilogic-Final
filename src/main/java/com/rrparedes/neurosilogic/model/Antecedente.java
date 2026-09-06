package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "antecedente")
public class Antecedente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAntecedente")
    private Long idAntecedente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdPaciente", nullable = false)
    private Paciente paciente;

    // Relación real a la enfermedad inscrita (cuando el antecedente es "Patológico"), en vez de
    // guardar el nombre de la enfermedad concatenado dentro del texto libre de "Observacion".
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEnfermedad")
    private Enfermedad enfermedad;

    @Column(name = "Tipo", length = 50)
    private String tipo;

    @Column(name = "Alergias", length = 255)
    private String alergias;

    @Column(name = "MedicamentosActuales", length = 255)
    private String medicamentosActuales;

    @Column(name = "Observacion", columnDefinition = "TEXT")
    private String observacion;

    @Column(name = "FechaRegistro")
    private LocalDateTime fechaRegistro;

    public Antecedente() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getIdAntecedente() {
        return idAntecedente;
    }

    public void setIdAntecedente(Long idAntecedente) {
        this.idAntecedente = idAntecedente;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Enfermedad getEnfermedad() {
        return enfermedad;
    }

    public void setEnfermedad(Enfermedad enfermedad) {
        this.enfermedad = enfermedad;
    }

    public String getTipo() {
        return tipo != null ? tipo : "Patológico";
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAlergias() {
        return alergias != null ? alergias : "Ninguna";
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getMedicamentosActuales() {
        return medicamentosActuales != null ? medicamentosActuales : "Ninguno";
    }

    public void setMedicamentosActuales(String medicamentosActuales) {
        this.medicamentosActuales = medicamentosActuales;
    }

    public String getObservacion() {
        return observacion;
    }

    // Compone la descripción visible a partir de la relación real con Enfermedad (si aplica),
    // en vez de depender de que el nombre de la enfermedad venga incrustado en "Observacion".
    public String getDescripcion() {
        if (enfermedad != null) {
            String nombre = enfermedad.getNombreEnfermedad();
            return (observacion != null && !observacion.isBlank()) ? nombre + " - " + observacion : nombre;
        }
        return observacion != null ? observacion : alergias;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setDescripcion(String descripcion) {
        this.observacion = descripcion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
