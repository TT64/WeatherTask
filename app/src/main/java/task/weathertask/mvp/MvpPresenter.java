package task.weathertask.mvp;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void destroy();
}
