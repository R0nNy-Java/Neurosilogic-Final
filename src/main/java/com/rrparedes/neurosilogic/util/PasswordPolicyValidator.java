package com.rrparedes.neurosilogic.util;

/**
 * Valida que una contraseña cumpla la política mínima de seguridad del sistema:
 * al menos 8 caracteres, una letra mayúscula y un carácter especial.
 */
public final class PasswordPolicyValidator {

    public static final String MENSAJE_REQUISITOS =
            "La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un carácter especial.";

    private PasswordPolicyValidator() {
    }

    public static boolean esValida(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean tieneMayuscula = false;
        boolean tieneEspecial = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) tieneMayuscula = true;
            else if (!Character.isLetterOrDigit(c)) tieneEspecial = true;
        }
        return tieneMayuscula && tieneEspecial;
    }
}
