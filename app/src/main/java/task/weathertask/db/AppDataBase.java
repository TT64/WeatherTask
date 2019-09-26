package task.weathertask.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = Weather.class, version = 1)
public abstract class AppDataBase extends RoomDatabase {

    private static AppDataBase instance;

    public abstract WeatherDao weatherDao();

}
