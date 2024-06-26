package com.example.weatherapp.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        binding.setViewModel(weatherViewModel);
        binding.setLifecycleOwner(this);

        webView = binding.webview;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new CustomWebViewClient());

        binding.getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = binding.cityEditText.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    weatherViewModel.fetchWeatherData(city);
                    loadWeatherWebsite(city);
                }
            }
        });

        weatherViewModel.getWeatherData().observe(this, new Observer<WeatherResponse>() {
            @Override
            public void onChanged(WeatherResponse weatherResponse) {
                if (weatherResponse != null) {
                    binding.weatherTextView.setText("City: " + weatherResponse.getCityName());
                    binding.textView.setText(weatherResponse.getMain().getTemp() + "°C");
                    binding.textView2.setText("Description: " + weatherResponse.getWeather().get(0).getDescription());
                    setBackground(weatherResponse.getWeather().get(0).getDescription());
                } else {
                    Log.e(TAG, "WeatherResponse is null");
                }
            }
        });
    }

    private void setBackground(String description) {
        binding.getRoot().setBackgroundResource(getBackgroundResource(description));
    }

    private int getBackgroundResource(String description) {
        if (description.contains("clear sky")) {
            return R.drawable.clear_sky;
        } else if (description.contains("few clouds")) {
            return R.drawable.few_clouds;
        } else if (description.contains("scattered clouds")) {
            return R.drawable.scattered_clouds;
        } else if (description.contains("broken clouds")) {
            return R.drawable.broken_clouds;
        } else if (description.contains("shower rain")) {
            return R.drawable.shower_rain;
        } else if (description.contains("rain") || (description.contains("light rain"))) {
            return R.drawable.rain;
        } else if (description.contains("thunderstorm")) {
            return R.drawable.thunderstorm;
        } else if (description.contains("snow")) {
            return R.drawable.snow;
        } else if (description.contains("mist")) {
            return R.drawable.mist;
        } else {
            return R.drawable.weatherbg;
        }
    }

    private void loadWeatherWebsite(String city) {
        String formattedCity = city.replaceAll("\\s+", "-"); // Replace spaces with hyphens for URL
        String url = "https://www.theweather.com/" + formattedCity.toLowerCase().trim() + ".htm";
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.loadUrl("javascript:(function() { " +
                    "var elements = document.getElementsByClassName('ad'); " +
                    "for (var i = 0; i < elements.length; i++) { " +
                    "   elements[i].style.display='none'; " +
                    "}})();");
        }
    }
}
