package com.rrparedes.neurosilogic.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordUtil {

    public static String hash(String text) {
        if (text == null) return "";
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

    public static boolean verificar(String textPlano, String textHash) {
        if (textPlano == null || textHash == null) return false;
        if (textHash.length() < 30) { // Si no está encriptado en la semilla antigua
            return textPlano.equals(textHash);
        }
        return hash(textPlano).equalsIgnoreCase(textHash);
    }
}
