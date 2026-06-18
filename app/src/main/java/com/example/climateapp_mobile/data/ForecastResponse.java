package com.example.climateapp_mobile.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {

    @SerializedName("current")
    public Current current;

    @SerializedName("daily")
    public Daily daily;

    public static class Current {
        @SerializedName("temperature_2m")
        public double temperature;

        @SerializedName("apparent_temperature")
        public double feelsLike;

        @SerializedName("relative_humidity_2m")
        public int humidity;

        @SerializedName("wind_speed_10m")
        public double windSpeed;

        @SerializedName("weather_code")
        public int weatherCode;
    }

    public static class Daily {
        @SerializedName("time")
        public List<String> time;

        @SerializedName("temperature_2m_max")
        public List<Double> tempMax;

        @SerializedName("temperature_2m_min")
        public List<Double> tempMin;

        @SerializedName("weather_code")
        public List<Integer> weatherCode;

    }
}
