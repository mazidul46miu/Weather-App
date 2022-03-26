package com.example.weatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.databinding.ForecastrowBinding;
import com.example.weatherapp.forecast.ListItem;
import com.example.weatherapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>{
    private List<ListItem>forecastItem =new ArrayList<>();
    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ForecastrowBinding binding = ForecastrowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ForecastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        final ListItem item = forecastItem.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return forecastItem.size();
    }

    public void submitList(List<ListItem> items) {
        forecastItem = items;
        notifyDataSetChanged();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder{
        private ForecastrowBinding binding;

        public ForecastViewHolder(@NonNull ForecastrowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ListItem item){
            binding.rowDateTimeTV.setText(
                    new SimpleDateFormat("EEE HH:mm")
                            .format(new Date(item.getDt()*1000L)));

            final String iconUrl = Constants.ICON_PREFIX+
                    item.getWeather().get(0).getIcon()+ Constants.ICON_SUFFIX;
            Picasso.get().load(iconUrl).into(binding.rowIconIV);

            binding.rowMaxMinTV.setText(
                    String.format("%.0f\u00B0/%.0f\u00B0",
                            item.getMain().getTempMax(),
                            item.getMain().getTempMin()));
        }
    }
}
