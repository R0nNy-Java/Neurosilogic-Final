package com.rrparedes.neurosilogic.model;

public class Dosificacion {

    // Método para homologar unidades a miligramos (mg) como base estándar
    private static double convertirAMg(double valor, String unidad) {
        if (unidad == null) return valor;
        switch (unidad.toLowerCase()) {
            case "g":
                return valor * 1000.0;     // 1 gramo = 1000 mg
            case "mcg":
                return valor / 1000.0;     // 1000 mcg = 1 mg
            case "mg":
            default:
                return valor;
        }
    }

    // Módulo 1: Regla de tres clínica
    public static double calcularVolumenAdministrar(double dosisIndicada, String unidadDosis,
                                                    double presentacion, String unidadPresentacion,
                                                    double diluyenteMl) {
        // Convertimos ambas a mg para que la regla de tres funcione con peras y peras
        double dosisMg = convertirAMg(dosisIndicada, unidadDosis);
        double presentacionMg = convertirAMg(presentacion, unidadPresentacion);

        if (presentacionMg <= 0) return 0.0;

        // Fórmula básica: (Dosis Solicitada * Diluyente) / Presentación
        return (dosisMg * diluyenteMl) / presentacionMg;
    }

    // Módulo 2: Infusiones y macro/micro goteos
    public static double calcularGotasPorMinuto(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        // Fórmula: Volumen / (Horas * 3)
        return volumenTotalMl / (horasTotales * 3.0);
    }

    public static double calcularMicrogotasPorMinuto(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        // Fórmula: Volumen / Horas (ya que 1 gota = 3 microgotas)
        return volumenTotalMl / horasTotales;
    }

    public static double calcularMlPorHora(double volumenTotalMl, double horasTotales) {
        if (horasTotales <= 0) return 0.0;
        return volumenTotalMl / horasTotales;
    }
}
