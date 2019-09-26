package task.weathertask.di;

import javax.inject.Singleton;

import dagger.Component;
import task.weathertask.WeatherApi;
import task.weathertask.db.WeatherDao;

@Singleton
@Component(modules = {NetModule.class, RoomModule.class})
public interface UtilsComponent {

    WeatherApi getWeatherApi();

    WeatherDao getWeatherDao();

}
