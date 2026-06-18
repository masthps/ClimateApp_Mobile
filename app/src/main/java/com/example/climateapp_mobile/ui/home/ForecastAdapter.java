package com.example.climateapp_mobile.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climateapp_mobile.R;
import com.example.climateapp_mobile.data.ForecastEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private final List<ForecastEntity> forecasts;

    public ForecastAdapter(List<ForecastEntity> forecasts) {
        this.forecasts = forecasts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forecast, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForecastEntity forecast = forecasts.get(position);
        holder.day.setText(formatDate(forecast.forecastDate));
        holder.description.setText(forecast.description);
        holder.temperature.setText(holder.itemView.getContext().getString(
                R.string.forecast_temperature,
                Math.round(forecast.tempMin),
                Math.round(forecast.tempMax)
        ));
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }

    private String formatDate(String date) {
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
        try {
            return displayFormat.format(apiFormat.parse(date));
        } catch (ParseException exception) {
            return date;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView day;
        final TextView description;
        final TextView temperature;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.tv_forecast_day);
            description = itemView.findViewById(R.id.tv_forecast_desc);
            temperature = itemView.findViewById(R.id.tv_forecast_temp);
        }
    }
}
