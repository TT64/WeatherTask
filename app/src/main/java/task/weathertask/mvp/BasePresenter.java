package task.weathertask.mvp;

public class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    private T view;

    @Override
    public void attachView(T mvpView) {
        view = mvpView;
    }

    @Override
    public void destroy() {
        view = null;
    }

    T getView() {
        return view;
    }
}
