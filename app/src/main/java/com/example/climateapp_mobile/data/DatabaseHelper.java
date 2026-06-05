package com.example.climateapp_mobile.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "climate_app.db";
    private static final int    DB_VERSION = 1;

    // Tabela clima atual
    public static final String TABLE_CURRENT    = "weather_current";
    // Tabela previsão
    public static final String TABLE_FORECAST   = "weather_forecast";

    private static final String CREATE_CURRENT =
            "CREATE TABLE " + TABLE_CURRENT + " (" +
                    "id          INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city_name   TEXT NOT NULL," +
                    "country_code TEXT," +
                    "temperature  REAL," +
                    "feels_like   REAL," +
                    "humidity     INTEGER," +
                    "description  TEXT," +
                    "icon_code    TEXT," +
                    "wind_speed   REAL," +
                    "timestamp    INTEGER NOT NULL" +
                    ");";

    private static final String CREATE_FORECAST =
            "CREATE TABLE " + TABLE_FORECAST + " (" +
                    "id            INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city_name     TEXT NOT NULL," +
                    "forecast_date TEXT NOT NULL," +
                    "temp_min      REAL," +
                    "temp_max      REAL," +
                    "humidity      INTEGER," +
                    "description   TEXT," +
                    "icon_code     TEXT," +
                    "timestamp     INTEGER NOT NULL" +
                    ");";

    // Singleton — evita múltiplas conexões
    private static DatabaseHelper instance;
    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null)
            instance = new DatabaseHelper(ctx.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CURRENT);
        db.execSQL(CREATE_FORECAST);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        // Estratégia simples para agora — evoluir nas próximas entregas
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORECAST);
        onCreate(db);
    }
}
