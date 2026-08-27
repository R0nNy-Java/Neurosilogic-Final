package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
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

    @Column(name = "Cedula", length = 10, nullable = false, unique = true)
    private String cedula;

    @Column(name = "Nombres", length = 50, nullable = false)
    private String nombres;

    @Column(name = "Apellidos", length = 50, nullable = false)
    private String apellidos;

    @Column(name = "FechaNacimiento")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

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
}
