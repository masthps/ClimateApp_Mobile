package com.example.climateapp_mobile.api;

import com.example.climateapp_mobile.data.GeocodingResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GeocodingApi {

    @GET("v1/search")
    Call<GeocodingResponse> searchCity(
            @Query("name") String cityName,
            @Query("count") int count,
            @Query("language") String language
    );
}
