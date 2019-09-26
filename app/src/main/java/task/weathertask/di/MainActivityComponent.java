package task.weathertask.di;

import dagger.Component;
import task.weathertask.MainActivity;

@PresenterScope
@Component(dependencies = {UtilsComponent.class}, modules = {MainActivityModule.class})
public interface MainActivityComponent {
    void injectMainActivity(MainActivity activity);
}
