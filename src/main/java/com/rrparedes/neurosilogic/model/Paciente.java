package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "paciente")
public class Paciente implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdPaciente")
    private Long idPaciente;

    @NotBlank(message = "La cédula es obligatoria")
    @Size(min = 10, max = 10, message = "La cédula debe tener exactamente 10 dígitos")
    @Column(name = "Cedula", length = 10, nullable = false, unique = true)
    private String cedula;

    @NotBlank(message = "Los nombres son obligatorios")
    @Column(name = "Nombres", length = 50, nullable = false)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column(name = "Apellidos", length = 50, nullable = false)
    private String apellidos;

    // No existe un campo "edad" persistido (se calcula en getEdad() a partir de esta fecha),
    // por lo que la restricción de edad válida [0-120] se aplica aquí como fecha no futura.
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @Column(name = "FechaNacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El sexo es obligatorio")
    @Column(name = "Sexo", length = 1, nullable = false)
    private String sexo;

    @Column(name = "Estado", length = 1)
    private String estado;

    public Paciente() {
    }

    public Paciente(String cedula, String nombres, String apellidos, LocalDate fechaNacimiento, String sexo, String estado) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.estado = estado;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Calcula dinámicamente la edad del paciente en base a la fecha de nacimiento actual.
     */
    public Integer getEdad() {
        if (this.fechaNacimiento == null) {
            return 0;
        }
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Retorna los apellidos abreviados formalmente (ej: "Pérez P." para "Pérez Paredes").
     */
    public String getApellidosAbreviados() {
        if (this.apellidos == null || this.apellidos.trim().isEmpty()) {
            return "";
        }
        String[] partes = this.apellidos.trim().split("\\s+");
        if (partes.length >= 2) {
            return partes[0] + " " + partes[1].substring(0, 1).toUpperCase() + ".";
        }
        return partes[0];
    }
}
