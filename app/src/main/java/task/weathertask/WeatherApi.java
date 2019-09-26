package task.weathertask;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("weather?")
    Call<WeatherModel> getWeatherById(@Query("id") int id, @Query("appid") String key, @Query("lang") String lang);

    @GET("weather?")
    Call<WeatherModel> getWeatherByLocation(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String key, @Query("lang") String lang);
}
