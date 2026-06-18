package com.example.climateapp_mobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherDao {

    private final DatabaseHelper dbHelper;

    public WeatherDao(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void insertOrReplaceCurrentWeather(WeatherEntity weather) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String storedCityName = findStoredCityName(db, DatabaseHelper.TABLE_CURRENT, weather.cityName);
        if (storedCityName != null) {
            db.delete(DatabaseHelper.TABLE_CURRENT, "city_name = ?", new String[]{storedCityName});
        }

        ContentValues values = new ContentValues();
        values.put("city_name", weather.cityName);
        values.put("country_code", weather.countryCode);
        values.put("temperature", weather.temperature);
        values.put("feels_like", weather.feelsLike);
        values.put("humidity", weather.humidity);
        values.put("description", weather.description);
        values.put("icon_code", weather.iconCode);
        values.put("wind_speed", weather.windSpeed);
        values.put("timestamp", weather.timestamp);
        db.insert(DatabaseHelper.TABLE_CURRENT, null, values);
    }

    public WeatherEntity getCurrentWeather(String cityName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String storedCityName = findStoredCityName(db, DatabaseHelper.TABLE_CURRENT, cityName);
        if (storedCityName == null) {
            return null;
        }

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_CURRENT,
                null,
                "city_name = ?",
                new String[]{storedCityName},
                null,
                null,
                "timestamp DESC",
                "1"
        )) {
            return cursor.moveToFirst() ? cursorToWeather(cursor) : null;
        }
    }

    public void insertForecasts(List<ForecastEntity> forecasts) {
        if (forecasts.isEmpty()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            String cityName = forecasts.get(0).cityName;
            String storedCityName = findStoredCityName(db, DatabaseHelper.TABLE_FORECAST, cityName);
            if (storedCityName != null) {
                db.delete(DatabaseHelper.TABLE_FORECAST, "city_name = ?", new String[]{storedCityName});
            }

            for (ForecastEntity forecast : forecasts) {
                ContentValues values = new ContentValues();
                values.put("city_name", forecast.cityName);
                values.put("forecast_date", forecast.forecastDate);
                values.put("temp_min", forecast.tempMin);
                values.put("temp_max", forecast.tempMax);
                values.put("description", forecast.description);
                values.put("icon_code", forecast.iconCode);
                values.put("timestamp", forecast.timestamp);
                db.insert(DatabaseHelper.TABLE_FORECAST, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ForecastEntity> getForecasts(String cityName) {
        List<ForecastEntity> forecasts = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String storedCityName = findStoredCityName(db, DatabaseHelper.TABLE_FORECAST, cityName);
        if (storedCityName == null) {
            return forecasts;
        }

        try (Cursor cursor = db.query(
                DatabaseHelper.TABLE_FORECAST,
                null,
                "city_name = ?",
                new String[]{storedCityName},
                null,
                null,
                "forecast_date ASC"
        )) {
            while (cursor.moveToNext()) {
                forecasts.add(cursorToForecast(cursor));
            }
        }
        return forecasts;
    }

    private String findStoredCityName(SQLiteDatabase db, String table, String cityName) {
        String normalizedSearch = normalizeCityName(cityName);
        try (Cursor cursor = db.query(true, table, new String[]{"city_name"},
                null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                String storedCityName = cursor.getString(0);
                if (normalizeCityName(storedCityName).equals(normalizedSearch)) {
                    return storedCityName;
                }
            }
        }
        return null;
    }

    private String normalizeCityName(String cityName) {
        String normalized = Normalizer.normalize(cityName.trim(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    private WeatherEntity cursorToWeather(Cursor cursor) {
        WeatherEntity weather = new WeatherEntity();
        weather.id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        weather.cityName = cursor.getString(cursor.getColumnIndexOrThrow("city_name"));
        weather.countryCode = cursor.getString(cursor.getColumnIndexOrThrow("country_code"));
        weather.temperature = cursor.getDouble(cursor.getColumnIndexOrThrow("temperature"));
        weather.feelsLike = cursor.getDouble(cursor.getColumnIndexOrThrow("feels_like"));
        weather.humidity = cursor.getInt(cursor.getColumnIndexOrThrow("humidity"));
        weather.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        weather.iconCode = cursor.getString(cursor.getColumnIndexOrThrow("icon_code"));
        weather.windSpeed = cursor.getDouble(cursor.getColumnIndexOrThrow("wind_speed"));
        weather.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
        return weather;
    }

    private ForecastEntity cursorToForecast(Cursor cursor) {
        ForecastEntity forecast = new ForecastEntity();
        forecast.id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
        forecast.cityName = cursor.getString(cursor.getColumnIndexOrThrow("city_name"));
        forecast.forecastDate = cursor.getString(cursor.getColumnIndexOrThrow("forecast_date"));
        forecast.tempMin = cursor.getDouble(cursor.getColumnIndexOrThrow("temp_min"));
        forecast.tempMax = cursor.getDouble(cursor.getColumnIndexOrThrow("temp_max"));
        forecast.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        forecast.iconCode = cursor.getString(cursor.getColumnIndexOrThrow("icon_code"));
        forecast.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
        return forecast;
    }
}
