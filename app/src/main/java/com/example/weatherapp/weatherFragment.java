package com.example.weatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.Preference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.databinding.FragmentWeatherBinding;
import com.example.weatherapp.permissions.LocationPermission;
import com.example.weatherapp.prefs.WeatherPreference;
import com.example.weatherapp.utils.Constants;
import com.example.weatherapp.viewmodels.WeatherViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


public class weatherFragment extends Fragment {
    private FusedLocationProviderClient providerClient;
    private String unitSymbol = "C";
    private FragmentWeatherBinding binding;
    private WeatherViewModel viewModel;
    private WeatherPreference preference;
    private ActivityResultLauncher<String > launcher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted ->{
                        if (isGranted){
                            detectUserLocation();
                        } else {
                            //show a dialog and explain user why you need this permission
                        }
                    });


    @SuppressLint("MissingPermission")
    private void detectUserLocation() {
        providerClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) return;
            viewModel.setLocation(location);
            viewModel.loadData();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            //Toast.makeText(getActivity(), "lat: "+latitude+",lon:"+longitude, Toast.LENGTH_SHORT).show();
            Log.e("WeatherApp","lat: "+latitude+",lon:"+longitude);
        });
    }


    public weatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.weather_menu,menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setQueryHint("Search a city");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setCity(query);
                viewModel.loadData();
                searchView.setQuery(null,false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== R.id.item_myLocation){
            viewModel.setCity(null);
            viewModel.loadData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater,container,false);
        viewModel = new ViewModelProvider(requireActivity()).get(WeatherViewModel.class);

        providerClient = LocationServices.getFusedLocationProviderClient(getActivity());
        preference = new WeatherPreference(getActivity());
        unitSymbol = preference.getTempStatus() ?"F" : "C";
        binding.tempUnitSwitch.setChecked(preference.getTempStatus());
        viewModel.setUnit(preference.getTempStatus());
        final ForecastAdapter adapter = new ForecastAdapter();
        final LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.HORIZONTAL);
        binding.forecastRV.setLayoutManager(llm);
        binding.forecastRV.setAdapter(adapter);

        if (LocationPermission.isLocationPermissionGranted(getActivity())){
            detectUserLocation();
        }else {
            LocationPermission.requestLocationPermission(launcher);
        }
        viewModel.getCurrentLiveData().observe(getViewLifecycleOwner(), currentResponseModel -> {

            binding.temperatureTv.setText(
                    String.format("%.0f\u00B0%s",
                            currentResponseModel.getMain().getTemp(),unitSymbol));

            binding.fellTV.setText(
                    String.format("feels like %.0f\u00B0",
                            currentResponseModel.getMain().getFeelsLike()));

            binding.maxTV.setText(
                    String.format("Max: %.0f\u00B0 / Min: %.0f\u00B0",
                            currentResponseModel.getMain().getTempMax(),
                            currentResponseModel.getMain().getTempMin()));

            binding.dateTv.setText(
                    new SimpleDateFormat("EEEE, MMMM dd, yyyy")
                            .format(new Date(currentResponseModel.getDt() * 1000L))
            );

            binding.cityTv.setText(currentResponseModel.getName());

            final String iconUrl = Constants.ICON_PREFIX+
                    currentResponseModel.getWeather().get(0).getIcon()+
                    Constants.ICON_SUFFIX;
            Picasso.get().load(iconUrl).into(binding.image);

            binding.sunny.setText(
                    currentResponseModel.getWeather().get(0).getDescription());

            binding.HumidityTV.setText("Humidity "+
                    currentResponseModel.getMain().getHumidity()+"%");

            binding.PressureTV.setText("Pressure "+
                    currentResponseModel.getMain().getPressure()+"hPa");

        });
        viewModel.getForecastLiveData().observe(getViewLifecycleOwner(),forecast ->{
            adapter.submitList(forecast.getList());
        });
        viewModel.getErrMsgLiveData().observe(getViewLifecycleOwner(),errMsg->{
            Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
        });
        binding.tempUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preference.setTempStatus(b);
                viewModel.setUnit(b);
                viewModel.loadData();
                unitSymbol = b ? "F" : "C";

            }
        });

        return binding.getRoot();
    }
}