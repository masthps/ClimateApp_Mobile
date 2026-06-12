package com.example.climateapp_mobile.ui.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climateapp_mobile.R;
import com.example.climateapp_mobile.data.ForecastEntity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tvCityName, tvTemperature, tvDescription,
            tvFeelsLike, tvHumidity, tvWind;
    private RecyclerView rvForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bindViews();
        loadMockCurrentWeather();
        loadMockForecast();
    }

    private void bindViews() {
        tvCityName    = findViewById(R.id.tv_city_name);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvDescription = findViewById(R.id.tv_description);
        tvFeelsLike   = findViewById(R.id.tv_feels_like);
        tvHumidity    = findViewById(R.id.tv_humidity);
        tvWind        = findViewById(R.id.tv_wind);
        rvForecast    = findViewById(R.id.rv_forecast);
    }

    private void loadMockCurrentWeather() {
        tvCityName.setText("São Paulo, BR");
        tvTemperature.setText("28°C");
        tvDescription.setText("Parcialmente nublado");
        tvFeelsLike.setText("Sensação\n30°C");
        tvHumidity.setText("Umidade\n65%");
        tvWind.setText("Vento\n12 km/h");
    }

    private void loadMockForecast() {
        List<ForecastEntity> mockList = new ArrayList<>();

        mockList.add(makeForecast("Segunda",  "Chuva",           24, 19));
        mockList.add(makeForecast("Terça",    "Ensolarado",      30, 22));
        mockList.add(makeForecast("Quarta",   "Nublado",         26, 20));
        mockList.add(makeForecast("Quinta",   "Parcial. nublado",28, 21));
        mockList.add(makeForecast("Sexta",    "Trovoada",        22, 18));

        ForecastAdapter adapter = new ForecastAdapter(mockList);
        rvForecast.setLayoutManager(new LinearLayoutManager(this));
        rvForecast.setAdapter(adapter);
    }

    private ForecastEntity makeForecast(String day, String desc,
                                        double max, double min) {
        ForecastEntity f = new ForecastEntity();
        f.forecastDate = day;
        f.description  = desc;
        f.tempMax      = max;
        f.tempMin      = min;
        return f;
    }
}
