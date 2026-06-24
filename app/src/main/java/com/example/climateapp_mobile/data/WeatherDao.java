package com.example.climateapp_mobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WeatherDao {

    private final DatabaseHelper dbHelper;

    public WeatherDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    // CLIMA ATUAL

    public void insertOrReplaceCurrentWeather(WeatherEntity weather) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_CURRENT, "city_name = ?", new String[]{weather.cityName});

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
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_CURRENT,
                null,
                "city_name = ?",
                new String[]{cityName},
                null,
                null,
                "timestamp DESC",
                "1"
        );

        WeatherEntity weather = null;
        if (cursor.moveToFirst()) {
            weather = cursorToWeather(cursor);
        }
        cursor.close();

        return weather;
    }

    // PREVISÃO

    public void insertForecasts(List<ForecastEntity> forecasts) {
        if (forecasts.isEmpty()) {
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            String cityName = forecasts.get(0).cityName;
            db.delete(DatabaseHelper.TABLE_FORECAST, "city_name = ?", new String[]{cityName});

            for (ForecastEntity forecast : forecasts) {
                ContentValues values = new ContentValues();
                values.put("city_name", forecast.cityName);
                values.put("forecast_date", forecast.forecastDate);
                values.put("temp_min", forecast.tempMin);
                values.put("temp_max", forecast.tempMax);
                values.put("humidity", forecast.humidity);
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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_FORECAST,
                null,
                "city_name = ?",
                new String[]{cityName},
                null,
                null,
                "forecast_date ASC"
        );

        List<ForecastEntity> forecasts = new ArrayList<>();
        while (cursor.moveToNext()) {
            forecasts.add(cursorToForecast(cursor));
        }
        cursor.close();

        return forecasts;
    }

    // HELPERS

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
        forecast.humidity = cursor.getInt(cursor.getColumnIndexOrThrow("humidity"));
        forecast.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        forecast.iconCode = cursor.getString(cursor.getColumnIndexOrThrow("icon_code"));
        forecast.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"));
        return forecast;
    }
}
