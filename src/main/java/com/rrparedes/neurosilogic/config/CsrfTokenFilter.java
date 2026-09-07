package com.rrparedes.neurosilogic.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Protección CSRF ligera (sin Spring Security completo, para no reestructurar el flujo de
 * autenticación por sesión ya existente justo antes de la entrega). Genera un token aleatorio
 * por sesión HTTP y exige que todo POST lo incluya como parámetro "csrfToken" y coincida con
 * el guardado en sesión — igual que el patrón "synchronizer token" clásico.
 *
 * Sin este filtro, cualquier sesión autenticada (enfermero/admin) podía ser explotada desde un
 * sitio externo con un formulario auto-enviado hacia cualquier endpoint POST del sistema
 * (bloquear usuarios, dar de alta pacientes, registrar signos vitales, etc.), ya que la única
 * defensa era la cookie de sesión, que el navegador de la víctima envía automáticamente.
 */
@Component
public class CsrfTokenFilter extends OncePerRequestFilter {

    public static final String SESSION_ATTR = "csrfToken";
    public static final String PARAM_NAME = "csrfToken";

    private final SecureRandom random = new SecureRandom();

    public String generarOTomarToken(HttpSession session) {
        Object existente = session.getAttribute(SESSION_ATTR);
        if (existente instanceof String token && !token.isBlank()) {
            return token;
        }
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(SESSION_ATTR, token);
        return token;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Asegura que toda petición tenga un token de sesión disponible para las vistas
        // (lo consume CsrfModelAdvice para inyectarlo en cada formulario).
        generarOTomarToken(request.getSession(true));

        String metodo = request.getMethod();
        boolean esMutacion = "POST".equalsIgnoreCase(metodo) || "PUT".equalsIgnoreCase(metodo)
                || "DELETE".equalsIgnoreCase(metodo) || "PATCH".equalsIgnoreCase(metodo);

        if (esMutacion) {
            String tokenEsperado = (String) request.getSession().getAttribute(SESSION_ATTR);
            String tokenRecibido = request.getParameter(PARAM_NAME);
            if (tokenEsperado == null || tokenRecibido == null || !tokenEsperado.equals(tokenRecibido)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("text/plain;charset=UTF-8");
                response.getWriter().write("Solicitud rechazada: token de seguridad ausente o inválido (CSRF). "
                        + "Vuelve a cargar la página e intenta de nuevo.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
