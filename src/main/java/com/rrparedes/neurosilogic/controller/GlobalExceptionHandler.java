package com.rrparedes.neurosilogic.controller;

import com.rrparedes.neurosilogic.service.NegocioException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
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

    private String rutaAnterior(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        return (referer != null && !referer.isBlank()) ? referer : "/dashboard";
    }
}
