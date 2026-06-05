package com.example.climateapp_mobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Weather {

    private final DatabaseHelper dbHelper;

    public Weather(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    // CLIMA ATUAL

    public void insertOrReplaceCurrentWeather(WeatherEntity w) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Remove entrada antiga da cidade antes de inserir
        db.delete(DatabaseHelper.TABLE_CURRENT, "city_name = ?", new String[]{w.cityName});

        ContentValues cv = new ContentValues();
        cv.put("city_name",    w.cityName);
        cv.put("country_code", w.countryCode);
        cv.put("temperature",  w.temperature);
        cv.put("feels_like",   w.feelsLike);
        cv.put("humidity",     w.humidity);
        cv.put("description",  w.description);
        cv.put("icon_code",    w.iconCode);
        cv.put("wind_speed",   w.windSpeed);
        cv.put("timestamp",    w.timestamp);
        db.insert(DatabaseHelper.TABLE_CURRENT, null, cv);
    }

    public WeatherEntity getCurrentWeather(String cityName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_CURRENT, null,
                "city_name = ?", new String[]{cityName},
                null, null, "timestamp DESC", "1");

        if (c.moveToFirst()) {
            WeatherEntity w = cursorToWeather(c);
            c.close();
            return w;
        }
        c.close();
        return null;
    }

    // PREVISÃO

    public void insertForecasts(List<ForecastEntity> forecasts) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Limpa previsões antigas da cidade
            if (!forecasts.isEmpty())
                db.delete(DatabaseHelper.TABLE_FORECAST, "city_name = ?",
                        new String[]{forecasts.get(0).cityName});

            for (ForecastEntity f : forecasts) {
                ContentValues cv = new ContentValues();
                cv.put("city_name",     f.cityName);
                cv.put("forecast_date", f.forecastDate);
                cv.put("temp_min",      f.tempMin);
                cv.put("temp_max",      f.tempMax);
                cv.put("humidity",      f.humidity);
                cv.put("description",   f.description);
                cv.put("icon_code",     f.iconCode);
                cv.put("timestamp",     f.timestamp);
                db.insert(DatabaseHelper.TABLE_FORECAST, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ForecastEntity> getForecasts(String cityName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.TABLE_FORECAST, null,
                "city_name = ?", new String[]{cityName},
                null, null, "forecast_date ASC");

        List<ForecastEntity> list = new ArrayList<>();
        while (c.moveToNext()) list.add(cursorToForecast(c));
        c.close();
        return list;
    }

    // HELPERS

    private WeatherEntity cursorToWeather(Cursor c) {
        WeatherEntity w = new WeatherEntity();
        w.id          = c.getLong(c.getColumnIndexOrThrow("id"));
        w.cityName    = c.getString(c.getColumnIndexOrThrow("city_name"));
        w.countryCode = c.getString(c.getColumnIndexOrThrow("country_code"));
        w.temperature = c.getDouble(c.getColumnIndexOrThrow("temperature"));
        w.feelsLike   = c.getDouble(c.getColumnIndexOrThrow("feels_like"));
        w.humidity    = c.getInt(c.getColumnIndexOrThrow("humidity"));
        w.description = c.getString(c.getColumnIndexOrThrow("description"));
        w.iconCode    = c.getString(c.getColumnIndexOrThrow("icon_code"));
        w.windSpeed   = c.getDouble(c.getColumnIndexOrThrow("wind_speed"));
        w.timestamp   = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        return w;
    }

    private ForecastEntity cursorToForecast(Cursor c) {
        ForecastEntity f = new ForecastEntity();
        f.id           = c.getLong(c.getColumnIndexOrThrow("id"));
        f.cityName     = c.getString(c.getColumnIndexOrThrow("city_name"));
        f.forecastDate = c.getString(c.getColumnIndexOrThrow("forecast_date"));
        f.tempMin      = c.getDouble(c.getColumnIndexOrThrow("temp_min"));
        f.tempMax      = c.getDouble(c.getColumnIndexOrThrow("temp_max"));
        f.humidity     = c.getInt(c.getColumnIndexOrThrow("humidity"));
        f.description  = c.getString(c.getColumnIndexOrThrow("description"));
        f.iconCode     = c.getString(c.getColumnIndexOrThrow("icon_code"));
        f.timestamp    = c.getLong(c.getColumnIndexOrThrow("timestamp"));
        return f;
    }
}
