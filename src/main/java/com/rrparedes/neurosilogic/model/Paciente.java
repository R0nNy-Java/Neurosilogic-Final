package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;

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

    @Column(name = "Edad", nullable = false)
    private Integer edad;

    @Column(name = "Sexo", length = 1, nullable = false)
    private String sexo;

    @Column(name = "Estado", length = 1)
    private String estado;

    public Paciente() {
    }

    public Paciente(String cedula, String nombres, String apellidos, Integer edad, String sexo, String estado) {
        this.cedula = cedula;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
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

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
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
