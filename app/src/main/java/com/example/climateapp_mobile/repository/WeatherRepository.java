package com.example.climateapp_mobile.repository;

import android.content.Context;

import com.example.climateapp_mobile.api.ApiClient;
import com.example.climateapp_mobile.api.GeocodingApi;
import com.example.climateapp_mobile.api.WeatherApi;
import com.example.climateapp_mobile.data.ForecastEntity;
import com.example.climateapp_mobile.data.ForecastResponse;
import com.example.climateapp_mobile.data.GeocodingResponse;
import com.example.climateapp_mobile.data.WeatherDao;
import com.example.climateapp_mobile.data.WeatherEntity;
import com.example.climateapp_mobile.util.WeatherUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class WeatherRepository {

    private static final long CACHE_DURATION_MS = 30 * 60 * 1000L;

    private final WeatherDao weatherDao;
    private final WeatherApi weatherApi;
    private final GeocodingApi geocodingApi;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface WeatherCallback {
        void onSuccess(WeatherEntity current, List<ForecastEntity> forecasts, boolean fromCache);

        void onError(String message);
    }

    public WeatherRepository(Context context) {
        weatherDao = new WeatherDao(context);
        weatherApi = ApiClient.getWeatherClient().create(WeatherApi.class);
        geocodingApi = ApiClient.getGeocodingClient().create(GeocodingApi.class);
    }

    public void fetchWeather(String cityName, WeatherCallback callback) {
        executor.execute(() -> {
            WeatherEntity cachedWeather = weatherDao.getCurrentWeather(cityName);
            if (cachedWeather != null && isCacheValid(cachedWeather.timestamp)) {
                callback.onSuccess(cachedWeather, weatherDao.getForecasts(cityName), false);
                return;
            }

            try {
                GeocodingResponse.Location location = fetchLocation(cityName);
                if (location == null) {
                    callback.onError("Cidade não encontrada. Confira o nome digitado e tente novamente.");
                    return;
                }

                ForecastResponse response = fetchForecast(location);
                if (!isForecastValid(response)) {
                    callback.onError("Não foi possível carregar a previsão completa. Tente novamente em instantes.");
                    return;
                }

                long timestamp = System.currentTimeMillis();
                WeatherEntity current = createCurrentWeather(location, response, timestamp);
                List<ForecastEntity> forecasts = createForecasts(location, response, timestamp);

                weatherDao.insertOrReplaceCurrentWeather(current);
                weatherDao.insertForecasts(forecasts);
                callback.onSuccess(current, forecasts, false);
            } catch (IOException exception) {
                returnCachedDataOrError(cityName, callback);
            } catch (RuntimeException exception) {
                callback.onError("Não foi possível processar os dados recebidos. Tente novamente.");
            }
        });
    }

    public void close() {
        executor.shutdownNow();
    }

    private GeocodingResponse.Location fetchLocation(String cityName) throws IOException {
        Response<GeocodingResponse> response = geocodingApi.searchCity(cityName, 1, "pt").execute();
        if (!response.isSuccessful() || response.body() == null
                || response.body().results == null || response.body().results.isEmpty()) {
            return null;
        }
        return response.body().results.get(0);
    }

    private ForecastResponse fetchForecast(GeocodingResponse.Location location) throws IOException {
        Response<ForecastResponse> response = weatherApi.getForecast(
                location.latitude,
                location.longitude,
                "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code",
                "temperature_2m_max,temperature_2m_min,weather_code",
                "auto",
                5,
                "kmh"
        ).execute();
        return response.isSuccessful() ? response.body() : null;
    }

    private boolean isForecastValid(ForecastResponse response) {
        if (response == null || response.current == null || response.daily == null
                || response.daily.time == null || response.daily.tempMax == null
                || response.daily.tempMin == null
                || response.daily.weatherCode == null || response.daily.time.isEmpty()) {
            return false;
        }

        int days = response.daily.time.size();
        return response.daily.tempMax.size() >= days
                && response.daily.tempMin.size() >= days
                && response.daily.weatherCode.size() >= days;
    }

    private WeatherEntity createCurrentWeather(GeocodingResponse.Location location,
                                               ForecastResponse response,
                                               long timestamp) {
        WeatherEntity weather = new WeatherEntity();
        weather.cityName = location.name;
        weather.countryCode = location.countryCode;
        weather.temperature = response.current.temperature;
        weather.feelsLike = response.current.feelsLike;
        weather.humidity = response.current.humidity;
        weather.windSpeed = response.current.windSpeed;
        weather.description = WeatherUtils.getDescription(response.current.weatherCode);
        weather.iconCode = String.valueOf(response.current.weatherCode);
        weather.timestamp = timestamp;
        return weather;
    }

    private List<ForecastEntity> createForecasts(GeocodingResponse.Location location,
                                                 ForecastResponse response,
                                                 long timestamp) {
        List<ForecastEntity> forecasts = new ArrayList<>();
        for (int index = 0; index < response.daily.time.size(); index++) {
            ForecastEntity forecast = new ForecastEntity();
            forecast.cityName = location.name;
            forecast.forecastDate = response.daily.time.get(index);
            forecast.tempMax = response.daily.tempMax.get(index);
            forecast.tempMin = response.daily.tempMin.get(index);
            forecast.description = WeatherUtils.getDescription(response.daily.weatherCode.get(index));
            forecast.iconCode = String.valueOf(response.daily.weatherCode.get(index));
            forecast.timestamp = timestamp;
            forecasts.add(forecast);
        }
        return forecasts;
    }

    private void returnCachedDataOrError(String cityName, WeatherCallback callback) {
        WeatherEntity cachedWeather = weatherDao.getCurrentWeather(cityName);
        if (cachedWeather != null) {
            callback.onSuccess(cachedWeather, weatherDao.getForecasts(cityName), true);
        } else {
            callback.onError("Sem conexão ou falha na busca. Não há previsão salva para esta cidade.");
        }
    }

    private boolean isCacheValid(long timestamp) {
        return System.currentTimeMillis() - timestamp < CACHE_DURATION_MS;
    }
}
