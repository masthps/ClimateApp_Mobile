package com.example.climateapp_mobile.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.climateapp_mobile.R;
import com.example.climateapp_mobile.data.ForecastEntity;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ViewHolder> {

    private final List<ForecastEntity> forecastList;

    public ForecastAdapter(List<ForecastEntity> forecastList) {
        this.forecastList = forecastList;
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
        ForecastEntity item = forecastList.get(position);
        holder.tvDay.setText(item.forecastDate);
        holder.tvDesc.setText(item.description);
        holder.tvTemp.setText(item.tempMin + "° / " + item.tempMax + "°");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDesc, tvTemp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay  = itemView.findViewById(R.id.tv_forecast_day);
            tvDesc = itemView.findViewById(R.id.tv_forecast_desc);
            tvTemp = itemView.findViewById(R.id.tv_forecast_temp);
        }
    }
}
