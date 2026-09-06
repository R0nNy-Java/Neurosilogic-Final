package com.rrparedes.neurosilogic.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /** Genera un hash BCrypt (con salt aleatorio incluido) para toda contraseña nueva. */
    public static String hash(String text) {
        if (text == null) return "";
        return ENCODER.encode(text);
    }

    public static boolean verificar(String textPlano, String textHash) {
        if (textPlano == null || textHash == null) return false;

        // Hash BCrypt (formato estándar, empieza con $2a$/$2b$/$2y$)
        if (textHash.startsWith("$2")) {
            return ENCODER.matches(textPlano, textHash);
        }

        // Compatibilidad con datos de la semilla antigua guardados sin encriptar
        if (textHash.length() < 30) {
            return textPlano.equals(textHash);
        }

        // Compatibilidad con hashes SHA-256 (sin salt) generados antes de migrar a BCrypt
        return sha256Legado(textPlano).equalsIgnoreCase(textHash);
    }

    private static String sha256Legado(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return text;
        }
    }
}
