package com.rrparedes.neurosilogic.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone el token CSRF de la sesión actual (generado por {@link CsrfTokenFilter}) como
 * atributo de modelo en TODAS las vistas, para que cualquier plantilla pueda incluir
 * &lt;input type="hidden" name="csrfToken" th:value="${csrfToken}"&gt; en sus formularios POST
 * sin que cada controlador tenga que agregarlo manualmente.
 */
@ControllerAdvice
public class CsrfModelAdvice {

    @ModelAttribute("csrfToken")
    public String csrfToken(HttpServletRequest request) {
        Object token = request.getSession(true).getAttribute(CsrfTokenFilter.SESSION_ATTR);
        return token != null ? token.toString() : "";
    }
}
