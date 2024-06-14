package com.example.weatherapp.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherapp.R;
import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.model.WeatherResponse;
import com.example.weatherapp.viewmodel.WeatherViewModel;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private WeatherViewModel weatherViewModel;
    private RelativeLayout rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        binding.setViewModel(weatherViewModel);
        binding.setLifecycleOwner(this);


        binding.getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binding.cityEditText.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    weatherViewModel.fetchWeatherData(city);
                }
            }
        });

        weatherViewModel.getWeatherData().observe(MainActivity.this, new Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse weatherResponse) {
                if (weatherResponse != null) {

                    binding.weatherTextView.setText("City: " + weatherResponse.getCityName());
                    binding.textView.setText(weatherResponse.getMain().getTemp() + "°C");
                    binding.textView2.setText("Description: " + weatherResponse.getWeather().get(0).getDescription());
                } else {
                    Log.e(TAG, "WeatherResponse is null");
                }
                setBackground(weatherResponse.getWeather().get(0).getDescription());
            }
        });
    }

    private void setBackground(String description) {
        rootView = findViewById(R.id.rootView);

        if (description.contains("clear sky")) {
            rootView.setBackgroundResource(R.drawable.clear_sky);
        } else if (description.contains("few clouds")) {
            rootView.setBackgroundResource(R.drawable.few_clouds);
        } else if (description.contains("scattered clouds")) {
            rootView.setBackgroundResource(R.drawable.scattered_clouds);
        } else if (description.contains("broken clouds")) {
            rootView.setBackgroundResource(R.drawable.broken_clouds);
        } else if (description.contains("shower rain")) {
            rootView.setBackgroundResource(R.drawable.shower_rain);
        } else if (description.contains("rain") || (description.contains("light rain"))) {
            rootView.setBackgroundResource(R.drawable.rain);
        } else if (description.contains("thunderstorm")) {
            rootView.setBackgroundResource(R.drawable.thunderstorm);
        } else if (description.contains("snow")) {
            rootView.setBackgroundResource(R.drawable.snow);
        } else if (description.contains("mist")) {
            rootView.setBackgroundResource(R.drawable.mist);
        } else {
            rootView.setBackgroundResource(R.drawable.weatherbg);
        }
    }
}
