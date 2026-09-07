package com.rrparedes.neurosilogic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(max = 30, message = "El nombre de usuario no puede superar los 30 caracteres")
    @Column(name = "NombreUsuario", length = 30, nullable = false, unique = true)
    private String nombreUsuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(name = "ContrasenaHash", length = 255, nullable = false)
    private String contrasenaHash;

    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Column(name = "Nombres", length = 50)
    private String nombres;

    @Size(max = 50, message = "El apellido no puede superar los 50 caracteres")
    @Column(name = "Apellidos", length = 50)
    private String apellidos;

    @Size(max = 20, message = "El rol no puede superar los 20 caracteres")
    @Column(name = "Rol", length = 20)
    private String rol;

    @Size(max = 1, message = "El estado debe ser un solo carácter")
    @Column(name = "Estado", length = 1)
    private String estado; // "A" = Activo, "B" = Bloqueado

    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 100, message = "El correo no puede superar los 100 caracteres")
    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "IntentosFallidos", nullable = false)
    private int intentosFallidos = 0;

    // Constructor por defecto (requerido por JPA)
    public Usuario() {
    }

    // Constructor 1: Mapeo exacto de base de datos JPA
    public Usuario(String nombreUsuario, String contrasenaHash, String nombres, String apellidos, String rol, String estado) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasenaHash;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.rol = rol;
        this.estado = estado;
    }

    // Constructor 2: Sobrecarga para compatibilidad con Servlets / UserStore
    public Usuario(String nombreUsuario, String contrasena, String rol, String nombreCompleto, String email, boolean bloqueado) {
        this.nombreUsuario = nombreUsuario;
        this.contrasenaHash = contrasena;
        this.rol = rol;
        setNombreCompleto(nombreCompleto);
        this.email = email;
        this.estado = bloqueado ? "B" : "A";
    }

    // Getters y Setters JPA estándar
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ── Métodos de compatibilidad para Servlets y UserStore ──

    public String getContrasena() {
        return contrasenaHash;
    }

    public void setContrasena(String contrasena) {
        this.contrasenaHash = contrasena;
    }

    public String getNombreCompleto() {
        if (nombres == null && apellidos == null) return "";
        if (apellidos == null || apellidos.trim().isEmpty()) return nombres != null ? nombres : "";
        if (nombres == null || nombres.trim().isEmpty()) return apellidos;
        return nombres + " " + apellidos;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            this.nombres = "";
            this.apellidos = "";
            return;
        }
        String[] partes = nombreCompleto.trim().split("\\s+", 2);
        this.nombres = partes[0];
        this.apellidos = partes.length > 1 ? partes[1] : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBloqueado() {
        return "B".equalsIgnoreCase(this.estado);
    }

    public void setBloqueado(boolean bloqueado) {
        this.estado = bloqueado ? "B" : "A";
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(int intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    // Centraliza el chequeo de rol administrador para no repetirlo en cada controlador
    public boolean isAdministrador() {
        return "ADMINISTRADOR".equalsIgnoreCase(rol) || "ADMIN".equalsIgnoreCase(rol);
    }
}
