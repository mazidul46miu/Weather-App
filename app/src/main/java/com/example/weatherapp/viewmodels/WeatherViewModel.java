package com.example.weatherapp.viewmodels;

import android.location.Location;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherapp.current.CurrentResponseModel;
import com.example.weatherapp.forecast.ForecastResponseModel;
import com.example.weatherapp.network.WeatherService;
import com.example.weatherapp.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private final String TAG = WeatherViewModel.class.getSimpleName();
    private Location location;
    private MutableLiveData<CurrentResponseModel>currentLiveData = new MutableLiveData<>();
    private MutableLiveData<ForecastResponseModel>forecastLiveData = new MutableLiveData<>();
    private MutableLiveData<String>errMsgLiveData = new MutableLiveData<>();
    private String unit = Constants.TEMP_UNIT_CELSIUS;
    private String city = null;

    public MutableLiveData<String> getErrMsgLiveData() {
        return errMsgLiveData;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setUnit(boolean isChecked){
        unit = isChecked ? Constants.TEMP_UNIT_FAHRENHEIT:
                Constants.TEMP_UNIT_CELSIUS;
    }

    public void loadData(){
        fetchCurrentData();
        fetchForecastData();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public MutableLiveData<CurrentResponseModel> getCurrentLiveData() {
        return currentLiveData;
    }

    public MutableLiveData<ForecastResponseModel> getForecastLiveData() {
        return forecastLiveData;
    }

    private void fetchCurrentData(){
        final String endUrl = city == null? String.format("weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(),location.getLongitude(),unit, Constants.WEATHER_API_KEY):
                String.format("weather?q=%s&units=%s&appid=%s", city,unit, Constants.WEATHER_API_KEY);
        WeatherService.getService().getCurrentData(endUrl).enqueue(new Callback<CurrentResponseModel>() {
            @Override
            public void onResponse(Call<CurrentResponseModel> call, Response<CurrentResponseModel> response) {
                if (response.code() == 200){
                    currentLiveData.postValue(response.body());
                }
                else if (response.code()==404){
                    errMsgLiveData.postValue(response.message());
                }
            }

            @Override
            public void onFailure(Call<CurrentResponseModel> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
            }

        });
    }
    private void fetchForecastData(){
        final String endUrl = city == null? String.format("forecast?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(),location.getLongitude(),unit, Constants.WEATHER_API_KEY):
                String.format("forecast?q=%s&appid=%s", city,unit, Constants.WEATHER_API_KEY);
        WeatherService.getService().getForecastData(endUrl).enqueue(new Callback<ForecastResponseModel>() {
            @Override
            public void onResponse(Call<ForecastResponseModel> call, Response<ForecastResponseModel> response) {
                if (response.code() == 200){
                    forecastLiveData.postValue(response.body());
                    //Log.e(TAG, "onResponse: "+forecastLiveData.getValue().toString());
                }

            }

            @Override
            public void onFailure(Call<ForecastResponseModel> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getLocalizedMessage());
            }
        });
    }



}
