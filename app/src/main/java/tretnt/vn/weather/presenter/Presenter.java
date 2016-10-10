package tretnt.vn.weather.presenter;
import tretnt.vn.weather.view.fragment.BaseFragment;

/**
 * Created by Trieu on 7/26/2016.
 */
public interface Presenter<V> {
    void attachTo(V mvpView);
    void detach();
}
