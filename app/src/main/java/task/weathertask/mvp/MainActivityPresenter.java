package task.weathertask.mvp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import task.weathertask.WeatherApi;
import task.weathertask.WeatherModel;
import task.weathertask.db.Weather;
import task.weathertask.db.WeatherDao;

public class MainActivityPresenter extends BasePresenter<MainActivityContract.View> implements MainActivityContract.Presenter, MainActivityContract.Model.onFinishListener {

    private MainActivityContract.Model model;
    private WeatherApi weatherApi;

    @Inject
    MainActivityPresenter(WeatherApi weatherApi, MainActivityContract.Model model) {
        this.model = model;
        this.weatherApi = weatherApi;
    }

    @Override
    public void onFinishResponseWeatherFromApi(WeatherModel weatherModel) {

        ArrayList<String> weatherData = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String sunriseStr, sunsetStr, windCardinal;

        sunriseStr = sdf.format(new Date(new Date(weatherModel.sys.getSunrise() * 1000L).getTime()));
        sunsetStr = sdf.format(new Date(new Date(weatherModel.sys.getSunset() * 1000L).getTime()));
        if (weatherModel.wind.getDeg() != null)
            windCardinal = degreesToCardinal(weatherModel.wind.getDeg());
        else
            windCardinal = "";

        weatherData.add(String.valueOf((int) (weatherModel.main.getTemp() - 273)));
        weatherData.add(weatherModel.main.getPressure() + " мм.рт.ст");
        weatherData.add(weatherModel.main.getHumidity() + " %");
        weatherData.add(sunriseStr);
        weatherData.add(sunsetStr);
        weatherData.add(weatherModel.weather.get(0).getDescription() + ", " + weatherModel.weather.get(0).getMain());
        weatherData.add(weatherModel.wind.getSpeed() + " м/с, " + windCardinal);
        weatherData.add(weatherModel.getName());
        getView().onResponseSuccessWeatherFromApi(weatherData);
    }

    @Override
    public void onFailureResponseWeatherFromApi(String error) {
        getView().onResponseErrorWeatherFromApi(error);
    }

    @Override
    public void onFinishResponseWeatherFromDatabase(Weather weather) {
        if (getView() != null) {
            getView().onResponseSuccessWeatherFromDatabase(weather);
        }
    }

    @Override
    public void onFailureResponseWeatherFromDatabase(String error) {
        if (getView() != null) {
            getView().onResponseErrorWeatherFromDatabase();
        }
    }

    @Override
    public void onFinishResponseWeatherToDatabase() {
        if (getView() != null) {
            getView().onResponseSuccessWeatherToDatabase();
        }
    }

    @Override
    public void onFailureResponseWeatherToDatabase(String error) {
        if (getView() != null) {
            getView().onResponseErrorWeatherToDatabase();
        }
    }

    @Override
    public void getWeatherFromApi(int id) {
        model.requestWeatherFromApi(weatherApi, id, this);
    }

    @Override
    public void getWeatherFromApiByLocation(double lat, double lon) {
        model.requestWeatherFromApiByLocation(weatherApi, lat, lon, this);
    }

    @Override
    public void getWeatherFromDatabase(WeatherDao weatherDao) {
        model.requestWeatherFromDatabase(weatherDao, this);
    }

    @Override
    public void writeWeatherToDatabase(WeatherDao weatherDao, Weather weather) {
        model.requestWeatherToDatabase(weatherDao, weather, this);
    }

    private String degreesToCardinal(double degrees) {
        String[] caridnals = {"Северный", "Северо-восточный", "Восточный",
                "Юго-восточный", "Южный", "Юго-западный", "Западный", "Северо-западный", "Северный"};
        return caridnals[(int) Math.round((degrees % 360) / 45)];
    }
}
