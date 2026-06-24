package com.example.climateapp_mobile.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GeocodingResponse {

    @SerializedName("results")
    public List<Location> results;

    public static class Location {
        @SerializedName("name")
        public String name;

        @SerializedName("latitude")
        public double latitude;

        @SerializedName("longitude")
        public double longitude;

        @SerializedName("country")
        public String country;

        @SerializedName("country_code")
        public String countryCode;
    }
}
