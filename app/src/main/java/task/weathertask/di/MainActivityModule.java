package task.weathertask.di;

import dagger.Module;
import dagger.Provides;
import task.weathertask.mvp.MainActivityContract;
import task.weathertask.mvp.MainActivityModel;

@Module
class MainActivityModule {

    @Provides
    @PresenterScope
    MainActivityContract.Model provideModel() {
        return new MainActivityModel();
    }
}
