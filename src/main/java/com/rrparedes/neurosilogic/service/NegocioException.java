package com.rrparedes.neurosilogic.service;

/**
 * Excepción de negocio lanzada por la capa de servicios cuando una operación no puede
 * completarse por una regla del dominio (cédula duplicada, credenciales inválidas,
 * paciente inexistente, etc.). Los controladores la capturan y muestran el mensaje
 * en la vista correspondiente, sin exponer una pantalla de error genérica.
 */
public class NegocioException extends RuntimeException {
    public NegocioException(String message) {
        super(message);
    }
}
