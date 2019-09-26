package task.weathertask.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface WeatherDao {

    @Insert(onConflict = REPLACE)
    void insertWeather(Weather weather);

    @Query("SELECT * from weather")
    Single<Weather> getWeather();
}
