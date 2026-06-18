package com.example.climateapp_mobile.util;

public final class WeatherUtils {

    private WeatherUtils() {
    }

    public static String getDescription(int code) {
        if (code == 0) return "Céu limpo";
        if (code == 1 || code == 2) return "Parcialmente nublado";
        if (code == 3) return "Nublado";
        if (code == 45 || code == 48) return "Névoa";
        if (code >= 51 && code <= 57) return "Garoa";
        if (code >= 61 && code <= 67) return "Chuva";
        if (code >= 71 && code <= 77) return "Neve";
        if (code >= 80 && code <= 82) return "Pancadas de chuva";
        if (code == 85 || code == 86) return "Pancadas de neve";
        if (code == 95) return "Trovoada";
        if (code == 96 || code == 99) return "Trovoada com granizo";
        return "Condição desconhecida";
    }
}
