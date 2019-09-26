package task.weathertask.mvp;

import java.util.concurrent.Callable;

import androidx.annotation.NonNull;
import io.reactivex.observers.DisposableSingleObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Completable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import task.weathertask.Constants;
import task.weathertask.WeatherApi;
import task.weathertask.WeatherModel;
import task.weathertask.db.Weather;
import task.weathertask.db.WeatherDao;

public class MainActivityModel implements MainActivityContract.Model {
    @Override
    public void requestWeatherFromApi(WeatherApi weatherApi, int id, final onFinishListener onFinishListener) {
        weatherApi.getWeatherById(id, Constants.API_KEY, "ru").enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {
                if (response.body() != null) {
                    onFinishListener.onFinishResponseWeatherFromApi(response.body());
                } else
                    onFinishListener.onFailureResponseWeatherFromApi("error");
            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                onFinishListener.onFailureResponseWeatherFromApi(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void requestWeatherFromApiByLocation(WeatherApi weatherApi, double lat, double lon, final onFinishListener onFinishListener) {
        weatherApi.getWeatherByLocation(lat, lon, Constants.API_KEY, "ru").enqueue(new Callback<WeatherModel>() {
            @Override
            public void onResponse(@NonNull Call<WeatherModel> call, @NonNull Response<WeatherModel> response) {
                if (response.body() != null) {
                    onFinishListener.onFinishResponseWeatherFromApi(response.body());
                } else
                    onFinishListener.onFailureResponseWeatherFromApi("error");
            }

            @Override
            public void onFailure(@NonNull Call<WeatherModel> call, @NonNull Throwable t) {
                onFinishListener.onFailureResponseWeatherFromApi(t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void requestWeatherFromDatabase(final WeatherDao weatherDao, final onFinishListener onFinishListener) {
        weatherDao.getWeather()
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(new DisposableSingleObserver<Weather>() {
                    @Override
                    public void onSuccess(Weather weather) {
                        onFinishListener.onFinishResponseWeatherFromDatabase(weather);
                    }

                    @Override
                    public void onError(Throwable e) {
                        onFinishListener.onFailureResponseWeatherFromDatabase(e.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void requestWeatherToDatabase(final WeatherDao weatherDao, final Weather weather, final onFinishListener onFinishListener) {
        Completable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                weatherDao.insertWeather(weather);
                return null;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        onFinishListener.onFinishResponseWeatherToDatabase();
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onFinishListener.onFailureResponseWeatherToDatabase(throwable.getLocalizedMessage());
                    }
                })
                .subscribe();
    }
}
