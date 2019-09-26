package task.weathertask.di;

import android.app.Application;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import task.weathertask.Constants;
import task.weathertask.db.AppDataBase;
import task.weathertask.db.WeatherDao;

@Module
public class RoomModule {

    private AppDataBase appDataBase;

    public RoomModule(Application application) {
        appDataBase = Room.databaseBuilder(application, AppDataBase.class, Constants.DB_NAME)
                .build();
    }

    @Singleton
    @Provides
    AppDataBase provideDatabase() {
        return appDataBase;
    }

    @Singleton
    @Provides
    WeatherDao provideDao(AppDataBase dataBase) {
        return dataBase.weatherDao();
    }
}
