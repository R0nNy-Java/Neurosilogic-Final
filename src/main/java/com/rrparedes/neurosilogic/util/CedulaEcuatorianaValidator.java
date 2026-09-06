package com.rrparedes.neurosilogic.util;

/**
 * Valida cédulas de identidad ecuatorianas con el algoritmo oficial de Módulo 10.
 */
public final class CedulaEcuatorianaValidator {

    private CedulaEcuatorianaValidator() {
    }

    public static boolean esValida(String cedula) {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito > 6) {
            return false; // 0-5: persona natural, 6: institución pública
        }

        int[] coeficientes = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i)) * coeficientes[i];
            if (digito >= 10) digito -= 9;
            suma += digito;
        }

        int digitoVerificador = Character.getNumericValue(cedula.charAt(9));
        int residuo = suma % 10;
        int calculado = (residuo == 0) ? 0 : (10 - residuo);
        return calculado == digitoVerificador;
    }
}
