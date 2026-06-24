package com.example.climateapp_mobile.ui.home;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climateapp_mobile.R;
import com.example.climateapp_mobile.data.ForecastEntity;
import com.example.climateapp_mobile.data.WeatherEntity;
import com.example.climateapp_mobile.repository.WeatherRepository;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private EditText citySearch;
    private Button searchButton;
    private ProgressBar progressBar;
    private TextView errorText;
    private TextView cityName;
    private TextView temperature;
    private TextView description;
    private TextView feelsLike;
    private TextView humidity;
    private TextView wind;
    private RecyclerView forecastList;
    private View currentWeatherCard;
    private View forecastCard;

    private WeatherRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        repository = new WeatherRepository(this);
        bindViews();
        setupSearch();
        forecastList.setLayoutManager(new LinearLayoutManager(this));
        citySearch.setText(R.string.default_city);
        searchWeather();
    }

    private void bindViews() {
        citySearch = findViewById(R.id.et_city_search);
        searchButton = findViewById(R.id.btn_search);
        progressBar = findViewById(R.id.progress_bar);
        errorText = findViewById(R.id.tv_error);
        cityName = findViewById(R.id.tv_city_name);
        temperature = findViewById(R.id.tv_temperature);
        description = findViewById(R.id.tv_description);
        feelsLike = findViewById(R.id.tv_feels_like);
        humidity = findViewById(R.id.tv_humidity);
        wind = findViewById(R.id.tv_wind);
        forecastList = findViewById(R.id.rv_forecast);
        currentWeatherCard = findViewById(R.id.card_current);
        forecastCard = findViewById(R.id.card_forecast);
    }

    private void setupSearch() {
        searchButton.setOnClickListener(view -> searchWeather());
        citySearch.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWeather();
                return true;
            }
            return false;
        });
    }

    private void searchWeather() {
        String city = citySearch.getText().toString().trim();
        if (city.isEmpty()) {
            showError(getString(R.string.error_empty_city));
            return;
        }

        showLoading(true);
        errorText.setVisibility(View.GONE);
        repository.fetchWeather(city, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherEntity current, List<ForecastEntity> forecasts, boolean fromCache) {
                runOnUiThread(() -> showWeather(current, forecasts, fromCache));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(message);
                });
            }
        });
    }

    private void showWeather(WeatherEntity current, List<ForecastEntity> forecasts, boolean fromCache) {
        showLoading(false);
        cityName.setText(getString(R.string.city_country, current.cityName, current.countryCode));
        temperature.setText(getString(R.string.temperature, Math.round(current.temperature)));
        description.setText(current.description);
        feelsLike.setText(getString(R.string.feels_like, Math.round(current.feelsLike)));
        humidity.setText(getString(R.string.humidity, current.humidity));
        wind.setText(getString(R.string.wind, Math.round(current.windSpeed)));
        forecastList.setAdapter(new ForecastAdapter(forecasts));

        currentWeatherCard.setVisibility(View.VISIBLE);
        forecastCard.setVisibility(View.VISIBLE);
        if (fromCache) {
            showError(getString(R.string.offline_data_message));
        } else {
            errorText.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisibility(View.VISIBLE);
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        searchButton.setEnabled(!loading);
    }

    @Override
    protected void onDestroy() {
        repository.close();
        super.onDestroy();
    }
}
