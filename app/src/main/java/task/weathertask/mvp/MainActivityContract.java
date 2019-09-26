package task.weathertask.mvp;

import java.util.ArrayList;

import task.weathertask.WeatherApi;
import task.weathertask.WeatherModel;
import task.weathertask.db.Weather;
import task.weathertask.db.WeatherDao;

public class MainActivityContract {

    public interface Presenter extends MvpPresenter<View> {

        void getWeatherFromApi(int id);

        void getWeatherFromApiByLocation(double lat, double lon);

        void getWeatherFromDatabase(WeatherDao weatherDao);

        void writeWeatherToDatabase(WeatherDao weatherDao, Weather weather);

    }

    public interface View extends MvpView {
        void onResponseSuccessWeatherFromApi(ArrayList<String> weatherData);

        void onResponseErrorWeatherFromApi(String error);

        void onResponseSuccessWeatherFromDatabase(Weather weather);

        void onResponseErrorWeatherFromDatabase();

        void onResponseSuccessWeatherToDatabase();

        void onResponseErrorWeatherToDatabase();
    }

    public interface Model {
        interface onFinishListener {
            void onFinishResponseWeatherFromApi(WeatherModel weatherModel);

            void onFailureResponseWeatherFromApi(String error);

            void onFinishResponseWeatherFromDatabase(Weather weather);

            void onFailureResponseWeatherFromDatabase(String error);

            void onFinishResponseWeatherToDatabase();

            void onFailureResponseWeatherToDatabase(String error);
        }

        void requestWeatherFromApi(WeatherApi weatherApi, int id, onFinishListener onFinishListener);

        void requestWeatherFromApiByLocation(WeatherApi weatherApi, double lat, double lon, onFinishListener onFinishListener);

        void requestWeatherFromDatabase(WeatherDao weatherDao, onFinishListener onFinishListener);

        void requestWeatherToDatabase(WeatherDao weatherDao, Weather weather, onFinishListener onFinishListener);
    }
}
