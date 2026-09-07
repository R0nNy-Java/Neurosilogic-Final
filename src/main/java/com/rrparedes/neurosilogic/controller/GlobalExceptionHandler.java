package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.service.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

/**
 * Captura los errores de validación e integridad de datos que antes terminaban en la
 * pantalla de error 500 de Spring, y en su lugar redirige a la página anterior mostrando
 * un mensaje de error legible (usando el mismo atributo "error" que ya consumen las vistas).
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NegocioException.class)
    public String manejarNegocio(NegocioException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:" + rutaAnterior(request);
    }

    // Se dispara cuando un @ModelAttribute anotado con @Valid (ej. registro de Paciente) no cumple
    // las anotaciones de Bean Validation de la entidad.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String manejarValidacionFormulario(MethodArgumentNotValidException ex, HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));
        redirectAttributes.addFlashAttribute("error", mensaje.isBlank() ? "Datos inválidos en el formulario." : mensaje);
        return "redirect:" + rutaAnterior(request);
    }

    // Se dispara cuando Hibernate valida una entidad al hacer save()/persist() (validación automática
    // por tener spring-boot-starter-validation en el classpath) y algún valor viola una restricción
    // como @Range/@DecimalMin/@Positive — por ejemplo signos vitales fuera de rango clínico.
    @ExceptionHandler(ConstraintViolationException.class)
    public String manejarViolacionRestriccion(ConstraintViolationException ex, HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {
        String mensaje = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining(" | "));
        redirectAttributes.addFlashAttribute("error", mensaje.isBlank() ? "Los datos ingresados están fuera de los rangos permitidos." : mensaje);
        return "redirect:" + rutaAnterior(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String manejarArgumentoInvalido(IllegalArgumentException ex, HttpServletRequest request,
                                           RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage() != null ? ex.getMessage() : "Solicitud inválida.");
        return "redirect:" + rutaAnterior(request);
    }

    // Se dispara cuando se viola una restricción de la base de datos que no depende de Bean
    // Validation (longitud de columna excedida, clave foránea inexistente, unicidad violada
    // fuera de los chequeos manuales, etc.) — por ejemplo un nombre de paciente/usuario
    // demasiado largo que pasa las validaciones Java pero no cabe en la columna MySQL.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String manejarIntegridadDatos(DataIntegrityViolationException ex, HttpServletRequest request,
                                         RedirectAttributes redirectAttributes) {
        log.warn("Violación de integridad de datos en {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "No se pudo guardar la información: algún dato ingresado es demasiado largo o inválido para el campo correspondiente.");
        return "redirect:" + rutaAnterior(request);
    }

    // Red de seguridad final: cualquier excepción no anticipada por los manejadores anteriores
    // (antes terminaba en la página blanca de error 500 de Spring, exponiendo potencialmente
    // detalles internos). Se registra completa en el log del servidor para diagnóstico, pero al
    // usuario solo se le muestra un mensaje genérico y se le regresa a donde estaba.
    @ExceptionHandler(Exception.class)
    public String manejarErrorInesperado(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.error("Error no controlado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al procesar la solicitud. Intente de nuevo; si el problema persiste, contacte al administrador.");
        return "redirect:" + rutaAnterior(request);
    }

    private String rutaAnterior(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isBlank()) ? referer : "/dashboard";
    }
}
