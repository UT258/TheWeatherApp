package com.example.weatherapp.viewmodel;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.weatherapp.api.RetrofitClient;
import com.example.weatherapp.api.WeatherApi;
import com.example.weatherapp.model.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<WeatherResponse> weatherData;
    private static final String API_KEY = "a48e676ad71137b6cf39ca831907c788"; // Replace with your OpenWeatherMap API key

    public LiveData<WeatherResponse> getWeatherData() {
        if (weatherData == null) {
            weatherData = new MutableLiveData<>();
        }
        return weatherData;
    }

    public void fetchWeatherData(String city) {
        WeatherApi weatherApi = RetrofitClient.getRetrofitInstance().create(WeatherApi.class);
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(city, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherData.setValue(response.body());
                }
                else {
                    Log.e(TAG, "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Handle error
                Log.e(TAG, "Network request failed: ", t);
            }
        });
    }
}