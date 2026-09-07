package com.rrparedes.neurosilogic.service;

import com.rrparedes.neurosilogic.model.Usuario;

import java.util.List;

public interface UsuarioService {

    /** Valida credenciales, aplica el conteo/bloqueo por intentos fallidos y audita el intento. */
    Usuario autenticar(String usuario, String contrasena);

    void registrarLogout(Usuario usuario);

    /**
     * Crea una cuenta con rol fijo ENFERMERO. Lanza {@link NegocioException} si el usuario o la
     * cédula ya existen, si la cédula no es válida (Módulo 10 ecuatoriano), o si la contraseña no
     * cumple la política mínima de seguridad (8+ caracteres, mayúscula y carácter especial).
     */
    Usuario registrarCuenta(String usuario, String contrasena, String nombreCompleto, String email, String cedula);

    /**
     * Lanza {@link NegocioException} si la contraseña actual no coincide, o si la nueva contraseña
     * no cumple la política mínima de seguridad (8+ caracteres, mayúscula y carácter especial).
     */
    Usuario cambiarContrasena(Long idUsuario, String contrasenaActual, String nuevaContrasena);

    /**
     * Genera una contraseña temporal y la envía por correo al usuario. Lanza {@link NegocioException}
     * si el usuario no existe, no tiene correo registrado, o el envío de correo falla — en cuyo caso
     * no se aplica ningún cambio.
     */
    void recuperarContrasena(String usuario);

    /**
     * Busca la cuenta registrada con ese correo y le envía su nombre de usuario por email.
     * Lanza {@link NegocioException} si no existe ninguna cuenta con ese correo o si el envío falla.
     */
    void recuperarNombreUsuario(String email);

    List<Usuario> listarTodos();

    long contarTodos();

    /** Bloquea/desbloquea manualmente a un usuario y audita la acción a nombre de {@code actor}. */
    void cambiarEstado(Usuario actor, Long idUsuario, String nuevoEstado);

    /** Asigna un rol (ej. ENFERMERO, ADMINISTRADOR) a un usuario. */
    void asignarRol(Usuario actor, Long idUsuario, String nuevoRol);
}
