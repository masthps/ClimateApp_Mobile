package com.example.climateapp_mobile.repository;

import android.content.Context;

import com.example.climateapp_mobile.data.ForecastEntity;
import com.example.climateapp_mobile.data.WeatherDao;
import com.example.climateapp_mobile.data.WeatherEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherRepository {

    public interface WeatherCallback {
        void onSuccess(WeatherResult result);

        void onError(String message);
    }

    public static class WeatherResult {
        public WeatherEntity currentWeather;
        public List<ForecastEntity> forecasts;
        public boolean fromCache;
    }

    private static final String GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_URL = "https://api.open-meteo.com/v1/forecast";

    private final WeatherDao weatherDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WeatherRepository(Context context) {
        this.weatherDao = new WeatherDao(context);
    }

    public void loadWeather(String cityName, WeatherCallback callback) {
        executor.execute(() -> {
            String searchCity = cityName.trim();

            try {
                WeatherResult onlineResult = fetchOnlineWeather(searchCity);
                weatherDao.insertOrReplaceCurrentWeather(onlineResult.currentWeather);
                weatherDao.insertForecasts(onlineResult.forecasts);
                callback.onSuccess(onlineResult);
            } catch (Exception e) {
                WeatherResult offlineResult = fetchOfflineWeather(searchCity);

                if (offlineResult.currentWeather != null || !offlineResult.forecasts.isEmpty()) {
                    offlineResult.fromCache = true;
                    callback.onSuccess(offlineResult);
                } else {
                    callback.onError("Não foi possível buscar a previsão e não há dados salvos para esta cidade.");
                }
            }
        });
    }

    public void close() {
        executor.shutdown();
    }

    private WeatherResult fetchOnlineWeather(String cityName) throws Exception {
        JSONObject cityJson = findCity(cityName);
        double latitude = cityJson.getDouble("latitude");
        double longitude = cityJson.getDouble("longitude");
        String countryCode = cityJson.optString("country_code", "");

        String forecastUrl = FORECAST_URL +
                "?latitude=" + latitude +
                "&longitude=" + longitude +
                "&current=temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min" +
                "&forecast_days=5" +
                "&timezone=auto";

        JSONObject forecastJson = readJson(forecastUrl);
        JSONObject currentJson = forecastJson.getJSONObject("current");
        JSONObject dailyJson = forecastJson.getJSONObject("daily");
        long timestamp = System.currentTimeMillis();

        WeatherEntity currentWeather = new WeatherEntity();
        currentWeather.cityName = cityName;
        currentWeather.countryCode = countryCode;
        currentWeather.temperature = currentJson.getDouble("temperature_2m");
        currentWeather.feelsLike = currentJson.getDouble("apparent_temperature");
        currentWeather.humidity = currentJson.getInt("relative_humidity_2m");
        currentWeather.windSpeed = currentJson.getDouble("wind_speed_10m");
        currentWeather.iconCode = String.valueOf(currentJson.getInt("weather_code"));
        currentWeather.description = getWeatherDescription(currentJson.getInt("weather_code"));
        currentWeather.timestamp = timestamp;

        List<ForecastEntity> forecasts = new ArrayList<>();
        JSONArray dates = dailyJson.getJSONArray("time");
        JSONArray codes = dailyJson.getJSONArray("weather_code");
        JSONArray maxTemperatures = dailyJson.getJSONArray("temperature_2m_max");
        JSONArray minTemperatures = dailyJson.getJSONArray("temperature_2m_min");

        for (int i = 0; i < dates.length(); i++) {
            int weatherCode = codes.getInt(i);

            ForecastEntity forecast = new ForecastEntity();
            forecast.cityName = cityName;
            forecast.forecastDate = dates.getString(i);
            forecast.tempMax = maxTemperatures.getDouble(i);
            forecast.tempMin = minTemperatures.getDouble(i);
            forecast.humidity = 0;
            forecast.iconCode = String.valueOf(weatherCode);
            forecast.description = getWeatherDescription(weatherCode);
            forecast.timestamp = timestamp;

            forecasts.add(forecast);
        }

        WeatherResult result = new WeatherResult();
        result.currentWeather = currentWeather;
        result.forecasts = forecasts;
        result.fromCache = false;
        return result;
    }

    private WeatherResult fetchOfflineWeather(String cityName) {
        WeatherResult result = new WeatherResult();
        result.currentWeather = weatherDao.getCurrentWeather(cityName);
        result.forecasts = weatherDao.getForecasts(cityName);
        result.fromCache = true;
        return result;
    }

    private JSONObject findCity(String cityName) throws Exception {
        String encodedCity = URLEncoder.encode(cityName, "UTF-8");
        String url = GEOCODING_URL + "?name=" + encodedCity + "&count=1&language=pt&format=json";
        JSONObject json = readJson(url);
        JSONArray results = json.optJSONArray("results");

        if (results == null || results.length() == 0) {
            throw new Exception("Cidade não encontrada.");
        }

        return results.getJSONObject(0);
    }

    private JSONObject readJson(String urlValue) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlValue).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } finally {
            connection.disconnect();
        }
    }

    private String getWeatherDescription(int code) {
        if (code == 0) {
            return "Céu limpo";
        } else if (code == 1 || code == 2 || code == 3) {
            return "Parcialmente nublado";
        } else if (code == 45 || code == 48) {
            return "Neblina";
        } else if (code >= 51 && code <= 67) {
            return "Garoa";
        } else if (code >= 71 && code <= 77) {
            return "Neve";
        } else if (code >= 80 && code <= 82) {
            return "Chuva";
        } else if (code >= 95 && code <= 99) {
            return "Tempestade";
        }

        return String.format(Locale.getDefault(), "Clima código %d", code);
    }
}
