package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "enfermedad")
public class Enfermedad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdEnfermedad")
    private Long idEnfermedad;

    @Column(name = "NombreEnfermedad", nullable = false, length = 150)
    private String nombreEnfermedad;

    @Column(name = "Categoria", length = 50)
    private String categoria;

    @Column(name = "Descripcion", columnDefinition = "TEXT")
    private String descripcion;

    public Enfermedad() {
    }

    public Enfermedad(Long idEnfermedad, String nombreEnfermedad, String categoria, String descripcion) {
        this.idEnfermedad = idEnfermedad;
        this.nombreEnfermedad = nombreEnfermedad;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public Long getIdEnfermedad() {
        return idEnfermedad;
    }

    public void setIdEnfermedad(Long idEnfermedad) {
        this.idEnfermedad = idEnfermedad;
    }

    public String getNombreEnfermedad() {
        return nombreEnfermedad;
    }

    public void setNombreEnfermedad(String nombreEnfermedad) {
        this.nombreEnfermedad = nombreEnfermedad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return nombreEnfermedad;
    }
}
