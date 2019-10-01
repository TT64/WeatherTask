package task.weathertask;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import task.weathertask.db.Weather;
import task.weathertask.db.WeatherDao;
import task.weathertask.di.DaggerMainActivityComponent;
import task.weathertask.mvp.MainActivityContract;
import task.weathertask.mvp.MainActivityPresenter;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View {

    @Inject
    public MainActivityPresenter presenter;

    @Inject
    public WeatherDao weatherDao;

    public String TAG = getClass().getSimpleName();
    public static final int LOCATION_PERMISSION_CODE = 999;

    private TextView tempTv, pressTv, humidTv, windTv, sunsetTv, sunriseTv, cloudTv, timeTv, cityTv;

    private Dialog loadDialog;

    private int cityId = -1;
    private boolean isStartLoad = false, isRestore = false;

    private SharedPreferences preferences;

    private FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initToolbar();

        DaggerMainActivityComponent.builder()
                .utilsComponent(((WeatherApp) getApplicationContext()).getUtilsComponent())
                .build().injectMainActivity(this);

        preferences = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        cityId = preferences.getInt(Constants.ID_KEY, -1);

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (savedInstanceState != null) {
            isRestore = true;
            timeTv.setText(savedInstanceState.getString(Constants.TIME_KEY));
            tempTv.setText(savedInstanceState.getString(Constants.TEMP_KEY));
            pressTv.setText(savedInstanceState.getString(Constants.PRESS_KEY));
            humidTv.setText(savedInstanceState.getString(Constants.HUM_KEY));
            sunriseTv.setText(savedInstanceState.getString(Constants.SUNR_KEY));
            sunsetTv.setText(savedInstanceState.getString(Constants.SUNS_KEY));
            cloudTv.setText(savedInstanceState.getString(Constants.CLOUD_KEY));
            windTv.setText(savedInstanceState.getString(Constants.WIND_KEY));
            cityTv.setText(savedInstanceState.getString(Constants.CITY_KEY));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        presenter.attachView(this);

        if (!isRestore) {
            startGetLocation();

            if (cityId != -1) {
                presenter.getWeatherFromDatabase(weatherDao);
            }
        }

        isStartLoad = true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.CITY_KEY, cityTv.getText().toString());
        outState.putString(Constants.TEMP_KEY, tempTv.getText().toString());
        outState.putString(Constants.TIME_KEY, timeTv.getText().toString());
        outState.putString(Constants.PRESS_KEY, pressTv.getText().toString());
        outState.putString(Constants.HUM_KEY, humidTv.getText().toString());
        outState.putString(Constants.SUNR_KEY, sunriseTv.getText().toString());
        outState.putString(Constants.SUNS_KEY, sunsetTv.getText().toString());
        outState.putString(Constants.CLOUD_KEY, cloudTv.getText().toString());
        outState.putString(Constants.WIND_KEY, windTv.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResponseSuccessWeatherFromApi(ArrayList<String> weatherData) {

        loadDialog.dismiss();

        long currentTimeMillis = System.currentTimeMillis();
        Date curDate = new Date(currentTimeMillis);
        String dateStr = getCurrentDate(curDate);

        String cityName = weatherData.get(7);

        timeTv.setText(getString(R.string.loadTime) + dateStr);
        tempTv.setText(weatherData.get(0) + getString(R.string.celcius));
        pressTv.setText(weatherData.get(1));
        humidTv.setText(weatherData.get(2));
        sunriseTv.setText(weatherData.get(3));
        sunsetTv.setText(weatherData.get(4));
        cloudTv.setText(weatherData.get(5));
        windTv.setText(weatherData.get(6));
        cityTv.setText(cityName);

        Weather weather = new Weather(dateStr, cityName, weatherData.get(0), weatherData.get(1), weatherData.get(2),
                weatherData.get(3), weatherData.get(4), weatherData.get(5), weatherData.get(6));
        weather.setId(1);

        presenter.writeWeatherToDatabase(weatherDao, weather);
    }

    @Override
    public void onResponseErrorWeatherFromApi(String error) {
        loadDialog.dismiss();
        presenter.getWeatherFromDatabase(weatherDao);
    }

    @Override
    public void onResponseSuccessWeatherFromDatabase(Weather weather) {
        cityTv.setText(weather.getCityName());
        timeTv.setText(getString(R.string.loadTime) + weather.getDate());
        tempTv.setText(weather.getTemperature() + getString(R.string.celcius));
        pressTv.setText(weather.getPressure());
        humidTv.setText(weather.getHumidity());
        sunriseTv.setText(weather.getSunriseTime());
        sunsetTv.setText(weather.getSunsetTime());
        cloudTv.setText(weather.getCloudnessMsg());
        windTv.setText(weather.getWindMsg());
    }

    @Override
    public void onResponseErrorWeatherFromDatabase() {
        Log.d(TAG, "Error get weather");
    }

    @Override
    public void onResponseSuccessWeatherToDatabase() {
        Log.d(TAG, "Success write");
    }

    @Override
    public void onResponseErrorWeatherToDatabase() {
        Log.d(TAG, "Error write");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (isLocationEnabled()) {
                            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        showLoadDialog();
                                        presenter.getWeatherFromApiByLocation(location.getLatitude(), location.getLongitude());
                                    } else
                                        getWeatherByManualSelection();
                                }
                            });
                        } else
                            showLocationOnDialog();
                    }
                } else {
                    cityId = 0;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(Constants.ID_KEY, cityId);
                    editor.apply();
                    Toast.makeText(this, getString(R.string.denyPermisson), Toast.LENGTH_LONG).show();
                    getWeatherByManualSelection();
                }
                break;
            }
        }
    }

    void initViews() {
        cityTv = findViewById(R.id.cityTv);
        timeTv = findViewById(R.id.timeTv);
        tempTv = findViewById(R.id.curTempVal);
        pressTv = findViewById(R.id.pressureValTv);
        humidTv = findViewById(R.id.humidityValTv);
        sunriseTv = findViewById(R.id.sunriseValTv);
        sunsetTv = findViewById(R.id.sunsetValTv);
        cloudTv = findViewById(R.id.cloudnessValTv);
        windTv = findViewById(R.id.windValTv);
    }

    void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private String getCurrentDate(Date curDate) {
        return new SimpleDateFormat("MM/yyyy HH:mm", Locale.ENGLISH).format(curDate);
    }

    private void showLocationOnDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LocationOnDialog locationOnDialog = LocationOnDialog.getInstance();
        locationOnDialog.show(ft, Constants.LOC_ON_TAG);
    }

    public void showChooseDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ChooseCityDialog chooseCityDialog = ChooseCityDialog.getInstance();
        chooseCityDialog.show(ft, Constants.CHS_TAG);
    }

    private void showLoadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(false);
        loadDialog = builder.create();
        loadDialog.show();
    }

    private boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void startGetLocation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                showLoadDialog();
                                presenter.getWeatherFromApiByLocation(location.getLatitude(), location.getLongitude());
                            } else {
                                getWeatherByManualSelection();
                            }
                        }
                    });
                } else {
                    showLocationOnDialog();
                }
            } else {
                checkLocationPermission();
            }
        } else {
            if (isLocationEnabled()) {
                locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            showLoadDialog();
                            presenter.getWeatherFromApiByLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            getWeatherByManualSelection();
                        }
                    }
                });
            } else
                showLocationOnDialog();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                LocationPermissionDialog locationPermissionDialog = LocationPermissionDialog.getInstance();
                locationPermissionDialog.show(getSupportFragmentManager(), Constants.LOC_PERM_TAG);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
        }
    }

    private void getWeatherByManualSelection() {
        if (cityId == -1) {
            if (isOnline()) {
                showChooseDialog();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.errorConnect), Toast.LENGTH_SHORT).show();
            }
        } else if (cityId > 0) {
            if (isOnline()) {
                if (!isStartLoad)
                    showChooseDialog();
                else {
                    showLoadDialog();
                    presenter.getWeatherFromApi(cityId);
                }
            } else {
                presenter.getWeatherFromDatabase(weatherDao);
            }
        } else if (cityId == 0) {
            if (!isStartLoad)
                showChooseDialog();
        }
        isStartLoad = false;
    }

    public void setCityId(int id) {
        cityId = id;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constants.ID_KEY, cityId);
        editor.apply();
        showLoadDialog();
        presenter.getWeatherFromApi(cityId);
    }

    public void refreshData(MenuItem item) {
        startGetLocation();
    }

}
