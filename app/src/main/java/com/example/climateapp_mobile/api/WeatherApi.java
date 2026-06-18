package com.example.climateapp_mobile.api;

import com.example.climateapp_mobile.data.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("v1/forecast")
    Call<ForecastResponse> getForecast(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("current") String current,
            @Query("daily") String daily,
            @Query("timezone") String timezone,
            @Query("forecast_days") int forecastDays,
            @Query("wind_speed_unit") String windSpeedUnit
    );
}
