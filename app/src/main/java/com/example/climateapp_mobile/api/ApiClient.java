package com.example.climateapp_mobile.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {

    private static final String WEATHER_BASE_URL = "https://api.open-meteo.com/";
    private static final String GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/";

    private static Retrofit weatherClient;
    private static Retrofit geocodingClient;

    private ApiClient() {
    }

    public static Retrofit getWeatherClient() {
        if (weatherClient == null) {
            weatherClient = createClient(WEATHER_BASE_URL);
        }
        return weatherClient;
    }

    public static Retrofit getGeocodingClient() {
        if (geocodingClient == null) {
            geocodingClient = createClient(GEOCODING_BASE_URL);
        }
        return geocodingClient;
    }

    private static Retrofit createClient(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
