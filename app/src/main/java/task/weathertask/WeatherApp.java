package task.weathertask;

import android.app.Application;

import task.weathertask.di.DaggerUtilsComponent;
import task.weathertask.di.NetModule;
import task.weathertask.di.RoomModule;
import task.weathertask.di.UtilsComponent;

public class WeatherApp extends Application {

    private UtilsComponent utilsComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        utilsComponent = DaggerUtilsComponent.builder()
                .netModule(new NetModule())
                .roomModule(new RoomModule(this))
                .build();
    }

    public UtilsComponent getUtilsComponent() {
        return utilsComponent;
    }
}
