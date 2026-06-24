package com.example.climateapp_mobile.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.climateapp_mobile.R;
import com.example.climateapp_mobile.data.ForecastEntity;
import com.example.climateapp_mobile.data.WeatherEntity;
import com.example.climateapp_mobile.repository.WeatherRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText cityInput;
    private Button searchButton;
    private ProgressBar progressBar;
    private TextView statusText;
    private TextView cityCountryText;
    private TextView temperatureText;
    private TextView descriptionText;
    private TextView feelsLikeText;
    private TextView humidityText;
    private TextView windText;
    private LinearLayout forecastList;

    private WeatherRepository weatherRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherRepository = new WeatherRepository(this);

        bindViews();
        setupEvents();
        loadDefaultCity();
    }

    private void bindViews() {
        cityInput = findViewById(R.id.et_city);
        searchButton = findViewById(R.id.btn_search);
        progressBar = findViewById(R.id.progress_bar);
        statusText = findViewById(R.id.tv_status);
        cityCountryText = findViewById(R.id.tv_city_country);
        temperatureText = findViewById(R.id.tv_temperature);
        descriptionText = findViewById(R.id.tv_description);
        feelsLikeText = findViewById(R.id.tv_feels_like);
        humidityText = findViewById(R.id.tv_humidity);
        windText = findViewById(R.id.tv_wind);
        forecastList = findViewById(R.id.forecast_list);
    }

    private void setupEvents() {
        searchButton.setOnClickListener(v -> searchWeather());
    }

    private void loadDefaultCity() {
        cityInput.setText(R.string.default_city);
        searchWeather();
    }

    private void searchWeather() {
        String cityName = cityInput.getText().toString().trim();

        if (cityName.isEmpty()) {
            statusText.setText(R.string.error_empty_city);
            statusText.setVisibility(View.VISIBLE);
            return;
        }

        showLoading(true);

        weatherRepository.loadWeather(cityName, new WeatherRepository.WeatherCallback() {
            @Override
            public void onSuccess(WeatherRepository.WeatherResult result) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showWeather(result);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    showLoading(false);
                    statusText.setText(message);
                    statusText.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        searchButton.setEnabled(!loading);

        if (loading) {
            statusText.setText(R.string.loading_weather);
            statusText.setVisibility(View.VISIBLE);
        }
    }

    private void showWeather(WeatherRepository.WeatherResult result) {
        WeatherEntity currentWeather = result.currentWeather;
        List<ForecastEntity> forecasts = result.forecasts;

        if (result.fromCache) {
            statusText.setText(R.string.offline_data_message);
            statusText.setVisibility(View.VISIBLE);
        } else {
            statusText.setVisibility(View.GONE);
        }

        if (currentWeather != null) {
            cityCountryText.setText(getString(
                    R.string.city_country,
                    currentWeather.cityName,
                    currentWeather.countryCode
            ));
            temperatureText.setText(getString(R.string.temperature, Math.round(currentWeather.temperature)));
            descriptionText.setText(currentWeather.description);
            feelsLikeText.setText(getString(R.string.feels_like, Math.round(currentWeather.feelsLike)));
            humidityText.setText(getString(R.string.humidity, currentWeather.humidity));
            windText.setText(getString(R.string.wind, Math.round(currentWeather.windSpeed)));
        }

        showForecasts(forecasts);
    }

    private void showForecasts(List<ForecastEntity> forecasts) {
        forecastList.removeAllViews();

        if (forecasts.isEmpty()) {
            TextView emptyText = createForecastText(getString(R.string.empty_forecast));
            forecastList.addView(emptyText);
            return;
        }

        for (ForecastEntity forecast : forecasts) {
            TextView forecastText = createForecastText(getString(
                    R.string.forecast_item,
                    formatDate(forecast.forecastDate),
                    forecast.description,
                    Math.round(forecast.tempMin),
                    Math.round(forecast.tempMax)
            ));
            forecastList.addView(forecastText);
        }
    }

    private TextView createForecastText(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(0, 12, 0, 12);
        return textView;
    }

    private String formatDate(String dateText) {
        try {
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat screenFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            Date date = apiFormat.parse(dateText);

            if (date != null) {
                return screenFormat.format(date);
            }
        } catch (ParseException ignored) {
            // Se a data vier em outro formato, mostra o valor original.
        }

        return dateText;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        weatherRepository.close();
    }
}
